package ru.maklas.mengine;

import com.badlogic.gdx.utils.Queue;
import org.jetbrains.annotations.Nullable;

/**
 * Remembers Entities after they were removed from engine.
 * Uses them to find by id if default EntityFinder failed.
 */
public class AfterRemoveEntityFinder implements EntityFinder, EntityListener {

    private final int maxMemory;
    private final Queue<Entity> memory;
    private final EntityFinder defaultFinder;

    /**
     * @param maxMemory How many Entities to keep track of after removal.
     * @param defaultFinder default implementation that will be used to find Entity by id
     */
    public AfterRemoveEntityFinder(int maxMemory, EntityFinder defaultFinder) {
        this.maxMemory = maxMemory;
        memory = new Queue<Entity>(maxMemory);
        this.defaultFinder = defaultFinder;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        defaultFinder.onAddedToEngine(engine);
        engine.addListener(this);
    }

    @Nullable
    @Override
    public Entity find(Engine engine, int id) {
        Entity e = defaultFinder.find(engine, id);
        if (e == null) {
            for (Entity entity : memory) {
                if (entity.id == id) {
                    e = entity;
                    break;
                }
            }
        }
        return e;
    }

    @Override
    public void entityAdded(Entity e) {

    }

    @Override
    public void entityRemoved(Entity e) {
        if (memory.size == maxMemory){
            memory.removeFirst();
        }
        memory.addLast(e);
    }
}
