package ru.maklas.mengine.test_systems;

import ru.maklas.mengine.Entity;
import ru.maklas.mengine.components.IRenderComponent;
import ru.maklas.mengine.systems.IterableZSortedRenderSystem;
import ru.maklas.mengine.test_components.RenderComponent;

public class RenderingSystem extends IterableZSortedRenderSystem{

    public RenderingSystem() {
        super(RenderComponent.class);
    }


    @Override
    protected void renderStarted() {

    }

    @Override
    protected void renderEntity(Entity entity, IRenderComponent rc) {
        System.out.println("Rendering: " + entity);
    }

    @Override
    protected void renderFinished() {

    }
}
