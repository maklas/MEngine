package ru.maklas.mengine.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.Subscription;

public class EventDispatcher {

    protected final ObjectMap<Class, Signal> map = new ObjectMap<Class, Signal>();
    protected final Array<Object> eventStack = new Array<Object>();

    /**
     * Subscribes to receive specific Event
     */
    @SuppressWarnings("all")
    public <T> void subscribe(Subscription<T> subscription){
        Signal<T> signal = map.get(subscription.clazz);
        if (signal == null){
            signal = new Signal<T>(5);
            map.put(subscription.clazz, signal);
        }
        signal.add(subscription);
    }

    @SuppressWarnings("all")
    public <T> void unsubscribe(Subscription<T> subscription){
        Signal<T> signal = map.get(subscription.clazz);
        if (signal != null){
            signal.remove(subscription);
        }
    }

    /**
     * Dispatches event. All Subscriptions that subscribed to this event will be notified.
     */
    @SuppressWarnings("all")
    public void dispatch(Object event){
        Signal signal = map.get(event.getClass());
        if (signal != null){
            eventStack.add(event);
            signal.dispatch(event);
            eventStack.pop();
        }
    }

    /**
     * Size of event stack. All events that are being fired at this moment.
     * Latest are on top
     */
    public int stackSize(){
        return eventStack.size;
    }

    /**
     * Root event that has been fired first on the stack.
     * Might throw an Exception if stackSize() == 0
     */
    public Object firstEvent(){
        return eventStack.first();
    }

    /**
     * Event that's being fired right now. Might throw Exception if stack's size is 0
     */
    public Object currentEvent(){
        return eventStack.peek();
    }

    /**
     * <p>
     * Use positive index to get events from the stack starting from the bottom
     * </p>
     * <p>
     * Use negative index to trace events back from current.
     * </p>
     * <p>
     *     Example:
     *
     *     <li>index = 0. Will return firstEvent()</li>
     *     <li>index = 1. Will return second event that was dispatched during firstEvent()</li>
     *     <li>index = -1. Will return the event that was dispatched right before currentEvent()</li>
     * </p>
     *
     * @throws RuntimeException if abs(index) >= stackSize()!
     */
    public Object getEvent(int index){
        if (index >= 0){
            return eventStack.get(index);
        } else {
            return eventStack.get(eventStack.size - 1 + index);
        }
    }

    public void clear() {
        map.clear();
    }
}
