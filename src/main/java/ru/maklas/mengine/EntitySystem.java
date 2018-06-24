package ru.maklas.mengine;

/**
 * System that iterates on Entities and their components each frame and mutates their state.
 */
public abstract class EntitySystem extends SubscriptionSystem {

    private boolean enabled = true;
    final int priority;

    public EntitySystem(int priority) {
        this.priority = priority;
    }

    public EntitySystem() {
        this(0);
    }

    public abstract void update(float dt);

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
