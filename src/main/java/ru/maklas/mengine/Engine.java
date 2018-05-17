package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.utils.EventDispatcher;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;

public class Engine implements Disposable {

    public static int TOTAL_COMPONENTS = 64;

    private final Array<Entity> entities;
    private final Array.ArrayIterator<Entity> getByIdIterator;
    final Array<UpdatableEntity> updatableEntities;
    private final ImmutableArray<Entity> immutableEntities;
    private final Listener<EntityComponentEvent> componentListener;
    private Array<EntityListener> listeners = new Array<EntityListener>();

    final SystemManager systemManager;
    private final GroupManager groupManager;
    EventDispatcher dispatcher;
    private Bundler bundler;

    boolean updating;
    private final DisposeOperation disposeOperation = new DisposeOperation();

    /**
     * Default Engine constructor
     */
    public Engine() {
        entities = new Array<Entity>(50);
        getByIdIterator = new Array.ArrayIterator<Entity>(entities);
        updatableEntities = new Array<UpdatableEntity>();
        immutableEntities = new ImmutableArray<Entity>(entities);
        systemManager = new SystemManager();
        groupManager = new GroupManager(this);
        dispatcher = new EventDispatcher();
        bundler = new Bundler();
        componentListener = new Listener<EntityComponentEvent>() {
            @Override
            public void receive(EntityComponentEvent event) {
                if (event.added){
                    groupManager.componentAdded(event.entity, event.mapper);
                } else {
                    groupManager.componentRemoved(event.entity, event.mapper);
                }
            }
        };
    }

    //*********************//
    //* ADDING + REMOVING *//
    //*********************//

    /**
     * Adds new Entity to the engine, notifying all EntityListeners
     */
    public Engine add(@NotNull Entity entity){
        if (entity.engine == this){
            return this;
        }
        if (entity instanceof UpdatableEntity){
            updatableEntities.add((UpdatableEntity) entity);
        }
        groupManager.entityAdded(entity);
        this.entities.add(entity);
        entity.componentSignal.add(componentListener);
        entity.addToEngine(this);
        for (EntityListener listener : listeners) {
            listener.entityAdded(entity);
        }

        return this;
    }

    /**
     * Adds Entity right away if not updating engine.
     * If engine is currently updating, Entity will wait for current updating system to finish it's update
     * before adding this Entity
     */
    public Engine addLater(@NotNull final Entity entity){
        if (updating){
            inUpdateQueue.addLast(new AddEntityOperation(entity));
            inUpdateDirty = true;
        } else add(entity);
        return this;
    }

    /**
     * Removes Entity right away if not updating engine.
     * If engine is currently updating, Entity will wait for current updating system to finish it's update
     * before removing this Entity
     */
    public Engine removeLater(@NotNull final Entity entity){
        if (updating){
            inUpdateQueue.addLast(new RemoveEntityOperation(entity));
            inUpdateDirty = true;
        } else remove(entity);
        return this;
    }

    /**
     * Removes Entity from Engine. Notifies listeners
     */
    public boolean remove(@NotNull Entity entity){
        if (entity.engine != this){
            return false;
        }
        if (entity instanceof UpdatableEntity){
            updatableEntities.removeValue((UpdatableEntity) entity, true);
        }
        groupManager.entityRemoved(entity);
        entity.componentSignal.remove(componentListener);
        entity.removeFromEngine();
        entities.removeValue(entity, true);

        for (EntityListener listener : listeners) {
            listener.entityRemoved(entity);
        }
        return true;
    }

    /**
     * Removes all Entities from the engine.
     * If you stumble upon Concurrency Exception, please try trigger it later after engine update is finished.
     */
    public void removeAllEntities(){
        final Entity[] entities = this.entities.toArray(Entity.class);
        for (Entity entity : entities) {
            remove(entity);
        }
    }

    /**
     * Adds new Entity system to engine. Will replace EntitySystem of the same class
     */
    public void add(@NotNull EntitySystem system){
        if (updating){
            afterUpdateQueue.addLast(new AddSystemOperation(system));
            return;
        }
        systemManager.addSystem(system);
        system.addToEngine(this);
    }

    /**
     * Removes specified EntitySystem by class
     */
    public void remove(@NotNull EntitySystem system){
        if (updating){
            afterUpdateQueue.addLast(new RemoveSystemOperation(system));
            return;
        }
        boolean b = systemManager.removeSystem(system);
        if (b){
            system.removeFromEngine();
        }
    }

    //***********//
    //* GETTERS *//
    //***********//

    /**
     * All entities that were added to engine
     */
    public ImmutableArray<Entity> getEntities(){
        return immutableEntities;
    }

    /**
     * GroupManager of this Engine
     */
    public GroupManager getGroupManager() {
        return groupManager;
    }

    /**
     * Finds first Entity with the said ID. Not efficient if there are too many Entities.
     */
    @Nullable
    public Entity getById(int id){
        Array.ArrayIterator<Entity> iterator = getByIdIterator;
        iterator.reset();
        while (iterator.hasNext()){
            Entity next = iterator.next();
            if (next.id == id){
                return next;
            }
        }
        return null;
    }

    public Bundler getBundler() {
        return bundler;
    }

    public void setBundler(Bundler bundler) {
        this.bundler = bundler;
    }

    /**
     * Uses GroupManager to receive Entity Array with specific component always present.
     * Returns the same instance for the same component
     */
    public ImmutableArray<Entity> entitiesFor(Class<? extends Component> componentClass) {
        return groupManager.of(componentClass).immutables;
    }

    /**
     * Event dispatcher for this Engine
     */
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Sets EventDispatcher for this Engine
     */
    public void setDispatcher(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    //*********************//
    //* EVENT DISPATCHING *//
    //*********************//

    /**
     * Subscribes for specific event. Don't forget to {@link #unsubscribe(Subscription) unsubscribe}
     * This subscription won't receive Events that are instance of Subscription type. Only Events that <b>are</b> of that type
     */
    public <T> void subscribe(Subscription<T> subscription) {
        dispatcher.subscribe(subscription);
    }

    /**
     * Subscribes for specific event. Don't forget to {@link #unsubscribe(Subscription) unsubscribe}
     * This subscription won't receive Events that are instance of Subscription type. Only Events that <b>are</b> of that type
     * @return Subscription that is used to unsubscribe later.
     */
    public <T> Subscription<T> subscribe(Class<T> eventClass, Listener<T> listener) {
        CompositSubscription<T> subscription = new CompositSubscription<T>(eventClass, listener);
        dispatcher.subscribe(subscription);
        return subscription;
    }

    /**
     * Unsubscribes for event.
     */
    public <T> void unsubscribe(Subscription<T> subscription){
        dispatcher.unsubscrive(subscription);
    }

    /**
     * Dispatches event in Engine. All Subscriptions that are subscribed to the class of this event will be called
     */
    public void dispatch(Object event){
        dispatcher.dispatch(event);
    }

    /**
     * Dispatches event and catches all events of specified class into array.
     * use {@link CatchResults#first()} to get first item or null if no events were caught
     */
    public <T> CatchResults<T> dispatchAndCatch(Object event, Class<T> catchEventClass){
        final CatchResults<T> results = new CatchResults<T>();
        Subscription<T> shortSubscription = new Subscription<T>(catchEventClass) {
            @Override
            public void receive(T e) {
                results.add(e);
            }
        };
        dispatcher.subscribe(shortSubscription);
        dispatch(event);
        dispatcher.unsubscrive(shortSubscription);
        return results;
    }

    /**
     * Executes this Runnable after all Systems are updated in {@link #update(float)}.
     * Is used internally if asked to remove System during update().
     * @param runnable - Runnable to be executed later after Systems get their update
     */
    public void executeAfterUpdate(Runnable runnable){
        afterUpdateQueue.addLast(runnable);
    }

    /**
     * if engine is not updating right now, this will be executed right away,
     * however if engine is updating, this Runnable will be called later, inbetween
     * EntitySystems update.
     * Also all {@link #dispatchLater(Object)} happen there
     * @param runnable - Runnable to be executed later after Systems get their update
     */
    public void executeLater(Runnable runnable){
        addToInUpdateIfUpdatingOrExecute(runnable);
    }

    /**
     * Dispatches event right away if Engine is not updating.
     * If updating, dispatches after next EntitySystem is finished updating
     */
    public void dispatchLater(final Object event){
        if (!updating){
            dispatch(event);
        } else {
            inUpdateDirty = true;
            inUpdateQueue.addLast(new DispatchOperation(event));
        }
    }

    final Queue<Runnable> afterUpdateQueue = new Queue<Runnable>();
    void processAfterUpdateOperations() {
        Queue<Runnable> pendingOperations = this.afterUpdateQueue;
        while (pendingOperations.size > 0){
            pendingOperations.removeFirst().run();
        }
    }

    final Queue<Runnable> inUpdateQueue = new Queue<Runnable>();
    boolean inUpdateDirty = false;
    void addToInUpdateIfUpdatingOrExecute(Runnable r){
        if (updating){
            inUpdateDirty = true;
            inUpdateQueue.addLast(r);
        } else {
            r.run();
        }
    }
    void processInUpdateOperations() {
        Queue<Runnable> pendingOperations = this.inUpdateQueue;
        while (pendingOperations.size > 0){
            pendingOperations.removeFirst().run();
        }
        inUpdateDirty = false;
    }

    public void addListener(EntityListener listener){
        listeners.add(listener);
    }

    public boolean removeListener(EntityListener listener){
        return listeners.removeValue(listener, true);
    }

    /**
     * Calls {@link RenderEntitySystem#invalidate()} on RenderSystem if present
     */
    public void invalidateRenderZ(){
        RenderEntitySystem renderSystem = systemManager.getRenderSystem();
        if (renderSystem != null){
            renderSystem.invalidate();
        }
    }

    //********//
    //* FLOW *//
    //********//

    /**
     * Updates all EntitySystems and dispatches delayed events.
     * @param dt last frame time
     */
    public void update(float dt){
        if(this.updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        updating = true;

        Array<EntitySystem> systems = systemManager.getSystems();

            for (EntitySystem system : systems) {
                if (system.isEnabled()) {
                    system.update(dt);
                }
                if (inUpdateDirty){
                    processInUpdateOperations();
                }
            }

        updating = false;
        processAfterUpdateOperations();
    }

    /**
     * Calls {@link RenderEntitySystem#render()} if System is present
     */
    public void render() {
        RenderEntitySystem renderSystem = systemManager.getRenderSystem();
        if (renderSystem != null){
            renderSystem.render();
        }
    }

    /**
     * Removes:
     * 1. All Entities;
     * 2. All Systems;
     * 3. Clears Dispatchers.
     * 4. All Listeners;
     * 5. Clears Bundler;
     */
    public void dispose(){
        if (updating){
            afterUpdateQueue.addLast(disposeOperation);
            return;
        }

        //Entities
        Entity[] entities = this.entities.toArray(Entity.class);
        for (Entity entity : entities) {
            remove(entity);
        }

        //Systems
        final EntitySystem[] allSystems = systemManager.getAll();
        for (EntitySystem system : allSystems) {
            remove(system);
        }

        this.dispatcher.clear();
        this.groupManager.clearAll();
        this.entities.clear();
        this.updatableEntities.clear();
        this.listeners.clear();
        this.bundler.clear();
    }

    private class AddSystemOperation implements Runnable{

        EntitySystem system;

        public AddSystemOperation(EntitySystem system) {
            this.system = system;
        }

        @Override
        public void run() {
            add(system);
        }
    }

    private class RemoveSystemOperation implements Runnable {

        private final EntitySystem system;

        public RemoveSystemOperation(EntitySystem system) {
            this.system = system;
        }

        @Override
        public void run() {
            remove(system);
        }
    }

    private class AddEntityOperation implements Runnable{

        Entity entity;

        public AddEntityOperation(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void run() {
            add(entity);
        }
    }

    private class RemoveEntityOperation implements Runnable {

        private final Entity entity;

        public RemoveEntityOperation(Entity e) {
            this.entity = e;
        }

        @Override
        public void run() {
            remove(entity);
        }
    }

    private class DisposeOperation implements Runnable{

        @Override
        public void run() {
            dispose();
        }
    }

    private class DispatchOperation implements Runnable{

        final Object event;

        public DispatchOperation(Object event) {
            this.event = event;
        }

        @Override
        public void run() {
            dispatch(event);
        }
    }
}
