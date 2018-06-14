package ru.maklas.mengine.performance_new.results;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.EntitySystem;

import java.util.Comparator;

public class FindByIdData {

    public long total;
    public int frames;
    public int calls;

    private ObjectMap<Class<? extends EntitySystem>, SystemCall> systemMap = new ObjectMap<Class<? extends EntitySystem>, SystemCall>();
    private static final Comparator<? super SystemCall> sorter = new Comparator<SystemCall>() {
        @Override
        public int compare(SystemCall o1, SystemCall o2) {
            return o2.calls - o1.calls;
        }
    };


    public Array<SystemCall> getTopUseSystems(){
        Array<SystemCall> arr = systemMap.values().toArray();
        arr.sort(sorter);
        return arr;
    }

    public SystemCall getForSystem(SystemData data){
        SystemCall systemByIdCall = systemMap.get(data.clazz);
        if (systemByIdCall == null){
            systemByIdCall = new SystemCall(data);
            systemMap.put(data.clazz, systemByIdCall);
        }
        return systemByIdCall;
    }


    public static class SystemCall {
        public SystemData data;
        public int calls;

        public SystemCall(SystemData data) {
            this.data = data;
        }
    }
}
