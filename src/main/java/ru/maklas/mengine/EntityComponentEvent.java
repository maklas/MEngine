package ru.maklas.mengine;

public final class EntityComponentEvent {

    Entity entity;
    Component component;
    ComponentMapper<? extends Component> mapper;
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


    EntityComponentEvent setUp(Entity entity, Component component, ComponentMapper<? extends Component> mapper, boolean added) {
        this.entity = entity;
        this.component = component;
        this.mapper = mapper;
        this.added = added;
        return this;
    }
}
