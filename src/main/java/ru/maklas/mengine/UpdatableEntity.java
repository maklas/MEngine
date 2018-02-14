package ru.maklas.mengine;

public abstract class UpdatableEntity extends Entity{

    public abstract void update(float dt);

    public UpdatableEntity(float x, float y, int zOrder) {
        super(x, y, zOrder);
    }

}
