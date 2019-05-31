package ru.maklas.mengine.utils;

/** A simple Listener interface used to listen to a {@link EventDispatcher} **/
public interface Listener<T> {
    /** @param e The event passed on dispatch **/
    void receive (T e);
}
