package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.utils.ImmutableArray;

import java.util.Comparator;

class SystemManager {

    private SystemComparator systemComparator = new SystemComparator();
    private Array<EntitySystem> systems = new Array<EntitySystem>(true, 16);
    private ObjectMap<Class<?>, EntitySystem> systemsByClass = new ObjectMap<Class<?>, EntitySystem>();

    public SystemManager() {

    }

    public boolean addSystem(EntitySystem system){
        Class<? extends EntitySystem> systemType = system.getClass();
        EntitySystem oldSytem = getSystem(systemType);

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
