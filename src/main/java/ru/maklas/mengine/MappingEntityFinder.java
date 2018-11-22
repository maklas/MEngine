package ru.maklas.mengine;

import com.badlogic.gdx.utils.IntMap;

/**
 * Only works if you always set ID before adding Entity into engine and it doesn't change
 */
public class MappingEntityFinder implements EntityFinder, EntityListener {

    IntMap<Entity> map = new IntMap<Entity>();

    public MappingEntityFinder(Engine engine) {
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
