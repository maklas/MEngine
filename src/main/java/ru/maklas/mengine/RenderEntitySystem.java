package ru.maklas.mengine;

public abstract class RenderEntitySystem extends EntitySystem {

    private boolean valid = false;
    private boolean alwaysInvalidate = false;

    public RenderEntitySystem() {
        super(Engine.RENDER_SYSTEM_PRIORITY);
    }

    @Override
    public final void update(float dt) {
        render();
    }

    public abstract void render();

    public final void invalidate(){
        this.valid = false;
    }

    public final boolean isAlwaysInvalidate() {
        return alwaysInvalidate;
    }

    public final void setAlwaysInvalidate(boolean alwaysInvalidate) {
        this.alwaysInvalidate = alwaysInvalidate;
    }

    public final boolean isValid(){
        return valid && !alwaysInvalidate;
    }


}
