package ru.maklas.mengine;

public abstract class UpdatableEntity extends Entity{

    public final EntityUpdateOrder updateOrder;

    public abstract void update();

    public UpdatableEntity(float x, float y, int zOrder) {
        super(x, y, zOrder);
        this.updateOrder = EntityUpdateOrder.AFTER_PHYSICS;
    }

    public UpdatableEntity(EntityUpdateOrder order) {
        this.updateOrder = order;
    }

    public UpdatableEntity() {
        this.updateOrder = EntityUpdateOrder.AFTER_PHYSICS;
    }

}
