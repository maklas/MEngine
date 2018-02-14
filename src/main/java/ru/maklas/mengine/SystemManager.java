package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.systems.CollisionEntitySystem;
import ru.maklas.mengine.systems.RenderEntitySystem;

import java.util.Comparator;

class SystemManager {

    private SystemComparator systemComparator = new SystemComparator();
    private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();
    private RenderEntitySystem renderSystem;
    private CollisionEntitySystem collisionSystem;

    public SystemManager() {

    }

    public boolean addSystem(EntitySystem system){
        Class<? extends EntitySystem> systemType = system.getClass();
        EntitySystem oldSytem = getSystem(systemType);
        if (system instanceof RenderEntitySystem){
            renderSystem = (RenderEntitySystem) system;
        }
        if (system instanceof CollisionEntitySystem){
            collisionSystem = (CollisionEntitySystem) system;
        }
        if (oldSytem != null) {
            removeSystem(oldSytem);
        }

        systems.add(system);
        systemsByClass.put(systemType, system);
        systems.sort(systemComparator);
        return true;
    }

    public boolean removeSystem(EntitySystem system){
        if(systems.removeValue(system, true)) {
            systemsByClass.remove(system.getClass());
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends EntitySystem> T getSystem(Class<T> systemType) {
        return (T) systemsByClass.get(systemType);
    }

    @Nullable
    public RenderEntitySystem getRenderSystem() {
        return renderSystem;
    }

    @Nullable
    public CollisionEntitySystem getCollisionSystem() {
        return collisionSystem;
    }

    public Array<EntitySystem> getSystems() {
        return systems;
    }

    private static class SystemComparator implements Comparator<EntitySystem> {
        @Override
        public int compare(EntitySystem a, EntitySystem b) {
            return a.priority > b.priority ? 1 : (a.priority == b.priority) ? 0 : -1;
        }
    }
}
