package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class SystemManager {

    private SystemComparator systemComparator = new SystemComparator();
    private Array<SubscriptionSystem> systems = new Array<SubscriptionSystem>(true, 32);
    private Array<EntitySystem> entitySystems = new Array<EntitySystem>(true, 32);
    private ObjectMap<Class<?>, SubscriptionSystem> systemsByClass = new ObjectMap<Class<?>, SubscriptionSystem>();
    private RenderEntitySystem renderSystem;

    SystemManager() {

    }

    boolean addSystem(SubscriptionSystem system){
        if (system instanceof RenderEntitySystem){
            renderSystem = (RenderEntitySystem) system;
            return true;
        }

        Class<? extends SubscriptionSystem> systemType = system.getClass();
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
        if (system == renderSystem){
            renderSystem = null;
        } else if (systems.removeValue(system, true)) {
            systemsByClass.remove(system.getClass());
            if (system instanceof EntitySystem) entitySystems.removeValue((EntitySystem) system, true);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends SubscriptionSystem> T getSystem(Class<T> systemType) {
        if (renderSystem != null && systemType == renderSystem.getClass()){
            return (T) renderSystem;
        }
        return (T) systemsByClass.get(systemType);
    }

    @Nullable
    public final RenderEntitySystem getRenderSystem() {
        return renderSystem;
    }


    /**
     * Do not edit!
     */
    public final Array<SubscriptionSystem> getSystems() {
        return systems;
    }

    /**
     * Do not edit!
     */
    public final Array<EntitySystem> getEntitySystems() {
        return entitySystems;
    }

    public final SubscriptionSystem[] getAll() {

        final int size = renderSystem == null ? systems.size : systems.size + 1;
        SubscriptionSystem[] ret = new EntitySystem[size];
        for (int i = 0; i < systems.size; i++) {
            ret[i] = systems.get(i);
        }

        if (renderSystem != null){
            ret[size - 1] = renderSystem;
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
