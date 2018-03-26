package ru.maklas.mengine.utils;

/**
 * A simple Listener interface used to listen to a {@link Signal}.
 * @author Stefan Bachmann
 */
public interface Listener<T> {
    /**
     * @param signal The Signal that triggered event
     * @param event The object passed on dispatch
     */
    public void receive (Signal<T> signal, T event);
}
