package ru.maklas.mengine;

import ru.maklas.mengine.utils.Listener;

/**
 * A subscription for specific Event.
 */
public abstract class Subscription<T> implements Listener<T> {

    public Class<T> clazz;

    /**
     * @param clazz Event class you want to subscribe to. Does not work with inherited Events.
     */
    public Subscription(Class<T> clazz) {
        this.clazz = clazz;
    }

}
