package ru.maklas.mengine;

import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public class CompositSubscription<T> extends Subscription<T> {

    private final Listener<T> listener;

    public CompositSubscription(Class<T> eventClass, Listener<T> listener) {
        super(eventClass);
        this.listener = listener;
    }

    @Override
    public void receive(Signal<T> signal, T event) {
        listener.receive(signal, event);
    }
}
