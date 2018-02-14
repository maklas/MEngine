package ru.maklas.mengine;

public abstract class EntitySystem {

    private boolean enabled = true;
    private Engine engine;
    final int priority;

    public EntitySystem(int priority) {
        this.priority = priority;
    }

    public EntitySystem() {
        this(0);
    }

    public final Engine getEngine() {
        return engine;
    }

    public void onAddedToEngine(Engine engine){}

    public void update(float dt){}

    void setEngine(Engine engine) {
        this.engine = engine;
        onAddedToEngine(engine);
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
