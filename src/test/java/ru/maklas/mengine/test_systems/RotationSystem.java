package ru.maklas.mengine.test_systems;

import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.test_components.Mappers;
import ru.maklas.mengine.test_components.RotationComponent;
import ru.maklas.mengine.utils.ImmutableArray;

public class RotationSystem extends EntitySystem{

    private ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        entities = engine.entitiesFor(RotationComponent.class);
    }

    @Override
    public void update(float dt) {
        ComponentMapper<RotationComponent> rotationM = Mappers.rotationM;

        for (Entity entity : entities) {
            entity.rotate(entity.get(rotationM).anglePerSecond * dt);
        }

    }
}
