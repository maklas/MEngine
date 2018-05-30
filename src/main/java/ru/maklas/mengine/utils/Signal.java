package ru.maklas.mengine.utils;

import com.badlogic.gdx.utils.SnapshotArray;

/**
 * A Signal is a basic event class that can dispatch an event to multiple listeners. It uses generics to allow any type of object
 * to be passed around on dispatch.
 * @author Stefan Bachmann
 */
public class Signal<T> {
    private final SnapshotArray<Listener<T>> listeners;

    public Signal () {
        listeners = new SnapshotArray<Listener<T>>();
    }

    /**
     * Add a Listener to this Signal
     * @param listener The Listener to be added
     */
    public void add (Listener<T> listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this Signal
     * @param listener The Listener to remove
     */
    public void remove (Listener<T> listener) {
        listeners.removeValue(listener, true);
    }

    /** Removes all listeners attached to this {@link Signal}. */
    public void removeAllListeners () {
        listeners.clear();
    }

    /**
     * Dispatches an event to all Listeners registered to this Signal
     * @param object The object to send off
     */
    public void dispatch (T object) {
        SnapshotArray<Listener<T>> listeners = this.listeners;

        final Object[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            Listener<T> listener = (Listener<T>)items[i];
            listener.receive(object);
        }
        listeners.end();
    }
}
