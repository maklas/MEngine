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

    public Engine getEngine() {
        return engine;
    }


    public void update(float dt){}

    void setEngine(Engine engine) {
        this.engine = engine;
        onAddedToEngine(engine);
    }

    public void onAddedToEngine(Engine engine){}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
