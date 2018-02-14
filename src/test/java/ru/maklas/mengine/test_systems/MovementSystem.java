package ru.maklas.mengine.test_systems;

import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.test_components.MovementComponent;
import ru.maklas.mengine.utils.ImmutableArray;

public class MovementSystem extends EntitySystem {

    ImmutableArray<Entity> entities;
    ComponentMapper<MovementComponent> mapper;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        entities = engine.entitiesFor(MovementComponent.class);
        mapper = ComponentMapper.of(MovementComponent.class);

    }

    @Override
    public void update(float dt) {
        ComponentMapper<MovementComponent> mapper = this.mapper;

        for (Entity entity : entities) {
            MovementComponent mc = entity.get(mapper);
            entity.x += mc.x * dt;
            entity.y += mc.y * dt;
        }

    }
}
