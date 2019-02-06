package ru.maklas.mengine;

import com.badlogic.gdx.utils.IntMap;

/**
 * Uses Map<Integer, Entity> to map Entity's ID to itself.
 * Only works if you always set ID before adding Entity into engine and it doesn't change.
 * Otherwise will produce bugs.
 */
public class MappingEntityFinder implements EntityFinder, EntityListener {

    private IntMap<Entity> map = new IntMap<Entity>();

    @Override
    public void onAddedToEngine(Engine engine) {
        engine.addListener(this);
    }

    @Override
    public Entity find(Engine engine, int id) {
        return map.get(id);
    }

    @Override
    public void entityAdded(Entity e) {
        map.put(e.id, e);
    }

    @Override
    public void entityRemoved(Entity e) {
        map.remove(e.id);
    }
}
