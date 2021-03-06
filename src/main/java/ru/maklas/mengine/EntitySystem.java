package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * <p>
 *     EntitySystem - is a system that iterates on Entities every frame and does action on them.
 * </p>
 * <p>
 *     Examples of EntitySystems:
 *     <li>MovementSystem</li>
 *     <li>PhysicsSystem</li>
 *     <li>DotDamageSystem</li>
 *     <li>{@link UpdatableEntitySystem}</li>
 * </p>
 * <p>
 *     System can be enabled and disabled with {@link #setEnabled(boolean)} method.
 *     If system is disabled, it will still receive events though, so it's on you.
 * </p>
 * @see SubscriptionSystem
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

    /**
     * Called every frame during {@link Engine#update(float)}
     * @param dt Delta time
     */
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
