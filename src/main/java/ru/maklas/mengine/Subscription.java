package ru.maklas.mengine;

import ru.maklas.mengine.utils.Listener;

public abstract class Subscription<T> implements Listener<T> {

    final Class<T> clazz;

    public Subscription(Class<T> clazz) {
        this.clazz = clazz;
    }
}
