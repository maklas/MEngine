package ru.maklas.mengine;

final class EntityComponentEvent {

    Entity entity;
    Component component;
    int mapperId;
    boolean added;

    public Entity getEntity() {
        return entity;
    }

    public Component getComponent() {
        return component;
    }

    public boolean added() {
        return added;
    }

    public boolean removed(){
        return !added;
    }


    EntityComponentEvent setUp(Entity entity, Component component, int mapperId, boolean added) {
        this.entity = entity;
        this.component = component;
        this.mapperId = mapperId;
        this.added = added;
        return this;
    }
}
