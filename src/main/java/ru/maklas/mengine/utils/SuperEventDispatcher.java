package ru.maklas.mengine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * SuperEventDispatcher is able to dispatch events to all subscriptions that subscribe to event's superclass.
 * This enables event inheritance. Also, before using it, you have to register all event classes
 * using {@link #registerClass(Class)} method. Otherwise it won't work.
 */
public class SuperEventDispatcher extends EventDispatcher {

    /** Maps event's Class to a list of all classes that are superclasses of this event **/
    protected final ObjectMap<Class, Array<Signal>> superClassSignals = new ObjectMap<Class, Array<Signal>>();

    /** Registers event's class to be used for multi-dispatching. **/
    public void registerClass(Class eventClass){
        Array<Signal> superclassSignals = superClassSignals.get(eventClass);
        if (superclassSignals != null) {
            return;
        }

        superclassSignals = new Array<Signal>();
        superClassSignals.put(eventClass, superclassSignals);
        superclassSignals.add(getSignalForClass(eventClass));

        try {
            Class superClass = eventClass.getSuperclass();
            while (superClass != null && !superClass.equals(Object.class)){
                superclassSignals.add(getSignalForClass(superClass));
                superClass = superClass.getSuperclass();
            }
        } catch (Throwable ignored) {}
    }

    /** Dispatches event. All Subscriptions that subscribed to this event will be notified **/
    @SuppressWarnings("all")
    public void dispatch(Object event){
        Array<Signal> signals = superClassSignals.get(event.getClass());
        if (signals != null){
            eventStack.add(event);
            for (Signal signal : signals) {
                signal.dispatch(event);
            }
            eventStack.pop();
        }
    }

    private Signal getSignalForClass(Class c){
        Signal s = map.get(c);
        if (s == null){
            s = new Signal(DEFAULT_CAPACITY);
            map.put(c, s);
        }
        return s;
    }
}
