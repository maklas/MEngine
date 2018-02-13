package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public class Engine {

    public static int TOTAL_COMPONENTS = 64;

    private final Array<Entity> entities;
    private final ImmutableArray<Entity> immutableEntities;
    private final SystemManager systemManager;
    private final GroupManager groupManager;
    private final Listener<EntityComponentEvent> componentListener;
    private boolean updating;

    Engine() {
        entities = new Array<Entity>(50);
        immutableEntities = new ImmutableArray<Entity>(entities);
        systemManager = new SystemManager();
        groupManager = new GroupManager();
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

    public void add(Entity entity){
        if (entity.getEngine() == this){
            return;
        }
        groupManager.entityAdded(entity);
        this.entities.add(entity);
        entity.componentSignal.add(componentListener);
        entity.addToEngine(this);
    }

    public boolean remove(Entity entity){
        if (entity.getEngine() != this){
            return false;
        }
        groupManager.entityRemoved(entity);
        entity.componentSignal.remove(componentListener);
        return entities.removeValue(entity, true);
    }

    public void add(EntitySystem system){
        if (updating){
            pendingOperations.addLast(new AddSystemOperation(system));
            return;
        }
        systemManager.addSystem(system);
        system.setEngine(this);
    }

    public void remove(EntitySystem system){
        if (updating){
            pendingOperations.addLast(new RemoveSystemOperation(system));
            return;
        }
        systemManager.removeSystem(system);
    }

    public ImmutableArray<Entity> getEntities(){
        return immutableEntities;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public void update(float dt){
        if(this.updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        updating = true;

        Array<EntitySystem> systems = systemManager.getSystems();

        for (EntitySystem system : systems) {
            if (system.isEnabled()){
                system.update(dt);
            }
        }


        updating = false;
        processPendingOperations();
    }


    private Queue<Runnable> pendingOperations = new Queue<Runnable>();
    private void processPendingOperations() {
        Queue<Runnable> pendingOperations = this.pendingOperations;
        while (pendingOperations.size > 0){
            pendingOperations.removeFirst().run();
        }
    }

    public void render() {

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
}
