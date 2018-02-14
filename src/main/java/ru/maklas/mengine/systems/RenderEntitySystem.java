package ru.maklas.mengine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;

public abstract class RenderEntitySystem extends EntitySystem {


    public RenderEntitySystem() {
        super(Engine.RENDER_SYSTEM_PRIORITY);
    }

    @Override
    public final void update(float dt) {
        render();
    }

    public abstract void render();




}
