package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

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
        priority = getPriorityFromMap(getClass());
    }

    public abstract void update(float dt);

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



    private static <K extends Class<? extends EntitySystem>> int getPriorityFromMap(K key){
        ObjectMap<Class<? extends EntitySystem>, Integer> map = Engine.systemOrderMap;
        Integer v = map.get(key);
        if (v != null) return v;

        try {
            Class superClass = key.getSuperclass();
            while (superClass != null && !superClass.equals(SubscriptionSystem.class)){
                Integer superClassValue = map.get(superClass);
                if (superClassValue != null) return superClassValue;
                superClass = superClass.getSuperclass();
            }
        } catch (Exception ignored) {}
        return 0;
    }

}
