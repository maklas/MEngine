package ru.maklas.mengine;

/**
 * Uses an array of specified size to find entities with 0 <= id < size.
 * Only works if you always set ID before adding Entity into engine and it doesn't change.
 */
public class FastEntityFinder implements EntityFinder, EntityListener {

    private final Entity[] array;
    private final EntityFinder defaultFinder;

    public FastEntityFinder(int size, EntityFinder defaultFinder) {
        this.defaultFinder = defaultFinder;
        array = new Entity[size];
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        engine.addListener(this);
    }

    @Override
    public Entity find(Engine engine, int id) {
        if (id >= 0 && id < array.length){
            return array[id];
        }
        return defaultFinder.find(engine, id);
    }

    @Override
    public void entityAdded(Entity e) {
        if (e.id >= 0 && e.id < array.length){
            array[e.id] = e;
        }
    }

    @Override
    public void entityRemoved(Entity e) {
        if (e.id >= 0 && e.id < array.length){
            array[e.id] = null;
        }
    }
}
