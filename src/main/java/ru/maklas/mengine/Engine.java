package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.utils.EventDispatcher;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

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
    private final Bundler bundler;

    boolean updating;
    private final DisposeOperation disposeOperation = new DisposeOperation();
    private Object userObject;

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
            pendingOperations.addLast(new AddSystemOperation(system));
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
            pendingOperations.addLast(new RemoveSystemOperation(system));
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

    /**
     * Gets user Object. Use it EntitySystem or Entity injections, since all Entities and Systems have access to the engine.
     * Most common use - model that contains your game data {player, internet socket, opponent, any other settings...}
     */
    public Object getUserObject() {
        return userObject;
    }

    /**
     * Sets user Object. Use it EntitySystem or Entity injections, since all Entities and Systems have access to the engine.
     * Most common use - model that contains your game data {player, internet socket, opponent, any other settings...}
     */
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
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
     * Dispatches event after {@link #update(float) update method} is finished
     */
    public void dispatchLater(final Object event){
        pendingOperations.addLast(new Runnable() {
            @Override
            public void run() {
                dispatcher.dispatch(event);
            }
        });
    }

    final Queue<Runnable> pendingOperations = new Queue<Runnable>();
    void processPendingOperations() {
        Queue<Runnable> pendingOperations = this.pendingOperations;
        while (pendingOperations.size > 0){
            pendingOperations.removeFirst().run();
        }
    }

    /**
     * Executes this Runnable after all Systems are updated in {@link #update(float)}.
     * Is used internally if asked to remove Entity/System during update().
     * Also all {@link #dispatchLater(Object)} happen there
     * @param runnable - Runnable to be executed later after Systems get their update
     */
    public void execureAfterUpdate(Runnable runnable){
        pendingOperations.addLast(runnable);
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
            }

        updating = false;
        processPendingOperations();
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
            pendingOperations.addLast(disposeOperation);
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

    private class DisposeOperation implements Runnable{

        @Override
        public void run() {
            dispose();
        }
    }
}
