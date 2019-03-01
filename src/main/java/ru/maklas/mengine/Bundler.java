package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Is like a Map to store User defined objects inside {@link Engine}.
 * It's much better to use Bundler rather than use System's constructor to pass data to it.
 */
public class Bundler {

    private final ObjectMap<String, Object> stringMap = new ObjectMap<String, Object>();
    private final ObjectMap<Class, Object> classMap = new ObjectMap<Class, Object>();

    public <T> void set(String name, T o){
        stringMap.put(name, o);
        classMap.put(o.getClass(), o);
    }

    public <T> void set(T o){
        this.classMap.put(o.getClass(), o);
    }

    public <T> void set(BundlerKey<T> key, T o){
        stringMap.put(key.key, o);
    }

    public <T> T get(BundlerKey<T> key){
        return (T) stringMap.get(key.key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String s){
        return (T) stringMap.get(s);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz){
        return (T) classMap.get(clazz);
    }

    public <T> T getAssert(String s){
        T o = get(s);
        if (o == null) throw new RuntimeException(s + " is required");
        return o;
    }

    public <T> T getAssert(Class<T> clazz){
        T o = get(clazz);
        if (o == null) throw new RuntimeException(clazz.getSimpleName() + " is required");
        return o;
    }

    public <T> T getAssert(BundlerKey<T> key){
        T o = get(key);
        if (o == null) throw new RuntimeException(key + " is required");
        return o;
    }

    /**
     * Returns integer value. 0 if key wasn't found
     */
    public int getInt(String key){
        return getInt(key, 0);
    }


    /**
     * Returns integer value. Default value if key wasn't found
     */
    public int getInt(String key, int def){
        Object o = stringMap.get(key);
        if (!(o instanceof Integer)){
            return def;
        }
        return (Integer) o;
    }

    /**
     * Returns boolean value. false if key wasn't found
     */
    public boolean getBool(String key){
        return getBool(key, false);
    }


    /**
     * Returns boolean value. def if key wasn't found
     */
    public boolean getBool(String key, boolean def){
        Object o = stringMap.get(key);
        if (!(o instanceof Boolean)){
            return def;
        }
        return (Boolean) o;
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
