package ru.maklas.mengine;

public abstract class UpdatableEntity extends Entity{

    /**
     * Called by {@link UpdatableEntitySystem}, so don't forget to add it to Engine
     * @param dt - frame delta time
     */
    public abstract void update(float dt);

    public UpdatableEntity(float x, float y, int layer) {
        super(x, y, layer);
    }

    public UpdatableEntity(int id, float x, float y, int layer) {
        super(id, x, y, layer);
    }

    public UpdatableEntity(int id) {
        super(id);
    }
}
