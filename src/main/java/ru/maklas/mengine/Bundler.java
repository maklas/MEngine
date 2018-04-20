package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

public class Bundler {

    private final ObjectMap<String, Object> stringMap = new ObjectMap<String, Object>();
    private final ObjectMap<Class, Object> classMap = new ObjectMap<Class, Object>();

    Bundler() {

    }

    public <T> void set(String name, T o){
        stringMap.put(name, o);
        classMap.put(o.getClass(), o);
    }

    public <T> void set(T o){
        this.classMap.put(o.getClass(), o);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String s){
        return (T) stringMap.get(s);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz){
        return (T) classMap.get(clazz);
    }

    public boolean remove(String s){
        Object o = stringMap.remove(s);
        if (o != null){
            classMap.remove(o.getClass());
        }
        return o != null;
    }

    public void clear(){
        stringMap.clear();
        classMap.clear();
    }
}
