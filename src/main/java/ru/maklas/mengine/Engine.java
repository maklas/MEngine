package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.utils.EventDispatcher;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public class Engine implements Disposable {

    public static int TOTAL_COMPONENTS = 64;
    public static final int RENDER_SYSTEM_PRIORITY = 2000;
    public static boolean UPDATE_ENTITIES_AFTER_ENGINE = true;

    private final DisposeOperation disposeOperation = new DisposeOperation();
    private final Array<Entity> entities;
    private final Array<UpdatableEntity> updatableEntities;
    private final ImmutableArray<Entity> immutableEntities;
    private final SystemManager systemManager;
    private final GroupManager groupManager;
    private final EventDispatcher dispatcher;
    private final Listener<EntityComponentEvent> componentListener;
    private boolean updating;
    private Array<EntityListener> listeners = new Array<EntityListener>();

    public Engine() {
        entities = new Array<Entity>(50);
        updatableEntities = new Array<UpdatableEntity>();
        immutableEntities = new ImmutableArray<Entity>(entities);
        systemManager = new SystemManager();
        groupManager = new GroupManager(this);
        dispatcher = new EventDispatcher();
        componentListener = new Listener<EntityComponentEvent>() {
            @Override
            public void receive(Signal<EntityComponentEvent> signal, EntityComponentEvent event) {
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

    public Engine add(Entity entity){
        if (entity.getEngine() == this){
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

    public boolean remove(Entity entity){
        if (entity.getEngine() != this){
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

    public void removeAllEntities(){
        final Entity[] entities = this.entities.toArray(Entity.class);
        for (Entity entity : entities) {
            remove(entity);
        }
    }

    public void add(EntitySystem system){
        if (updating){
            pendingOperations.addLast(new AddSystemOperation(system));
            return;
        }
        systemManager.addSystem(system);
        system.addToEngine(this);
    }

    public void remove(EntitySystem system){
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

    public ImmutableArray<Entity> getEntities(){
        return immutableEntities;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    @Nullable
    public Entity getById(int id){
        Array.ArrayIterator<Entity> iterator = new Array.ArrayIterator<Entity>(entities);
        while (iterator.hasNext()){
            Entity next = iterator.next();
            if (next.id == id){
                return next;
            }
        }
        return null;
    }

    public ImmutableArray<Entity> entitiesFor(Class<? extends Component> componentClass) {
        return groupManager.of(componentClass).immutables;
    }


    //*********************//
    //* EVENT DISPATCHING *//
    //*********************//



    public <T> void subscribe(Subscription<T> subscription) {
        dispatcher.subscribe(subscription);
    }

    public <T> void unsubscribe(Subscription<T> subscription){
        dispatcher.unsubscrive(subscription);
    }

    public void dispatch(Object event){
        dispatcher.dispatch(event);
    }

    /**
     * Dispatches event after current/next system update is finished
     */
    public void dispatchLater(final Object event){
        pendingOperations.addLast(new Runnable() {
            @Override
            public void run() {
                dispatcher.dispatch(event);
            }
        });
    }

    private Queue<Runnable> pendingOperations = new Queue<Runnable>();
    private void processPendingOperations() {
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

    public void invalidateRenderZ(){
        RenderEntitySystem renderSystem = systemManager.getRenderSystem();
        if (renderSystem != null){
            renderSystem.invalidate();
        }
    }

    //********//
    //* FLOW *//
    //********//

    public void update(float dt){
        if(this.updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        updating = true;

        Array<EntitySystem> systems = systemManager.getSystems();

        if (UPDATE_ENTITIES_AFTER_ENGINE) {
            for (EntitySystem system : systems) {
                if (system.isEnabled()) {
                    system.update(dt);
                }
            }

            for (UpdatableEntity updatableEntity : updatableEntities) {
                updatableEntity.update(dt);
            }

        } else {

            for (UpdatableEntity updatableEntity : updatableEntities) {
                updatableEntity.update(dt);
            }

            for (EntitySystem system : systems) {
                if (system.isEnabled()) {
                    system.update(dt);
                }
            }
        }


        updating = false;
        processPendingOperations();
    }

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
     * 4. All Listeners;
     * 3. Clears Dispatchers.
     */
    public void dispose(){
        if (updating){
            pendingOperations.addLast(disposeOperation);
            return;
        }

        Entity[] entities = this.entities.toArray(Entity.class);

        for (Entity entity : entities) {
            remove(entity);
        }

        final EntitySystem[] allSystems = systemManager.getAll();
        for (EntitySystem system : allSystems) {
            remove(system);
        }

        this.dispatcher.clear();
        this.groupManager.clearAll();
        this.entities.clear();
        this.updatableEntities.clear();
        this.listeners.clear();
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
