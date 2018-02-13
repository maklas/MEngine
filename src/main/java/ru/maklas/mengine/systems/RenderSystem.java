package ru.maklas.mengine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.components.RenderComponent;
import ru.maklas.mengine.utils.ImmutableArray;

public class RenderSystem extends EntitySystem {

    ImmutableArray<Entity> entities;

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.getGroupManager().of(RenderComponent.class).getEntities();
    }

    @Override
    public void update(float dt) {
        for (Entity entity : entities) {
            System.out.println(entity.id);
        }
    }
}
