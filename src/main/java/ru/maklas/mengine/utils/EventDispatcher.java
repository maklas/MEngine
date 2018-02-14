package ru.maklas.mengine.utils;

import com.badlogic.gdx.utils.ObjectMap;

public class EventDispatcher {

    private final ObjectMap<Class, Signal> map = new ObjectMap<Class, Signal>();

    @SuppressWarnings("all")
    public <T> void subscribe(Listener<T> listener, Class<T> type){
        Signal<T> signal = map.get(type);
        if (signal == null){
            signal = new Signal<T>();
            map.put(type, signal);
        }
        signal.add(listener);
    }

    @SuppressWarnings("all")
    public void dispatch(Object event){
        Signal signal = map.get(event.getClass());
        if (signal != null){
            signal.dispatch(event);
        }
    }


}
