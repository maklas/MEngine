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
        Integer priority = Engine.systemOrderMap.get(getClass());
        if (priority == null){
            this.priority = 0;
        } else {
            this.priority = priority;
        }
    }

    public abstract void update(float dt);

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
