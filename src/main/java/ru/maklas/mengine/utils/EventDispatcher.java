package ru.maklas.mengine.utils;

import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.Subscription;

public class EventDispatcher {

    private final ObjectMap<Class, Signal> map = new ObjectMap<Class, Signal>();

    @SuppressWarnings("all")
    public <T> void subscribe(Subscription<T> subscription){
        Signal<T> signal = map.get(subscription.clazz);
        if (signal == null){
            signal = new Signal<T>();
            map.put(subscription.clazz, signal);
        }
        signal.add(subscription);
    }

    @SuppressWarnings("all")
    public <T> void unsubscrive(Subscription<T> subscription){
        Signal<T> signal = map.get(subscription.clazz);
        if (signal != null){
            signal.remove(subscription);
        }
    }

    @SuppressWarnings("all")
    public void dispatch(Object event){
        Signal signal = map.get(event.getClass());
        if (signal != null){
            signal.dispatch(event);
        }
    }


    public void clear() {
        map.clear();
    }
}
