package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Stores Systems in Engine. Provides methods for their access
 */
public class SystemManager {

    private static final SystemComparator systemComparator = new SystemComparator();

    private Array<SubscriptionSystem> systems = new Array<SubscriptionSystem>(true, 64);
    private Array<EntitySystem> entitySystems = new Array<EntitySystem>(true, 32);
    private Array<RenderEntitySystem> renderSystems = new Array<RenderEntitySystem>(true, 10);
    private ObjectMap<Class<?>, SubscriptionSystem> systemsByClass = new ObjectMap<Class<?>, SubscriptionSystem>();

    SystemManager() {

    }

    boolean addSystem(SubscriptionSystem system){
        Class<? extends SubscriptionSystem> systemType = system.getClass();

        if (system instanceof RenderEntitySystem){
            renderSystems.add((RenderEntitySystem) system);
            systemsByClass.put(systemType, system);
            renderSystems.sort(systemComparator);
            return true;
        }

        SubscriptionSystem oldSystem = getSystem(systemType);
        if (oldSystem != null) {
            removeSystem(oldSystem);
        }

        systems.add(system);
        systemsByClass.put(systemType, system);
        if (system instanceof EntitySystem) {
            entitySystems.add((EntitySystem) system);
            entitySystems.sort(systemComparator);
        }
        return true;
    }

    boolean removeSystem(SubscriptionSystem system){

        if (system instanceof RenderEntitySystem){
            boolean removed = renderSystems.removeValue(((RenderEntitySystem) system), true);
            if (removed){
                systemsByClass.remove(system.getClass());
            }
            return removed;

        } else if (systems.removeValue(system, true)) {
            systemsByClass.remove(system.getClass());
            if (system instanceof EntitySystem) entitySystems.removeValue((EntitySystem) system, true);
            return true;
        }
        return false;
    }

    /**
     * Returns system by it's class.
     */
    @SuppressWarnings("unchecked")
    public <T extends SubscriptionSystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    public final Array<RenderEntitySystem> getRenderSystems() {
        return renderSystems;
    }


    /**
     * Do not edit this Array!
     */
    public final Array<SubscriptionSystem> getSystems() {
        return systems;
    }

    /**
     * Do not edit this Array!
     * @return All EntitySystems. <b>Not Subscription Systems, not RenderSystems</b>. To get all Systems use {@link #getAll()}
     */
    public final Array<EntitySystem> getEntitySystems() {
        return entitySystems;
    }

    /**
     * @return All Subscription Systems, Entity Systems and Render Systems.
     */
    public final SubscriptionSystem[] getAll() {

        final int size = systems.size + renderSystems.size;
        SubscriptionSystem[] ret = new SubscriptionSystem[size];
        for (int i = 0; i < systems.size; i++) {
            ret[i] = systems.get(i);
        }

        for (int i = 0; i < renderSystems.size; i++) {
            ret[i + systems.size] = renderSystems.get(i);
        }

        return ret;
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }
}
