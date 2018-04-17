package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.utils.Listener;

/**
 * System that iterates on Entities and their components each frame and mutates their state.
 */
public abstract class EntitySystem {

    private boolean enabled = true;
    private Engine engine;
    final int priority;
    private Array<Subscription> subscriptions;

    public EntitySystem(int priority) {
        this.priority = priority;
    }

    public EntitySystem() {
        this(0);
    }

    public final Engine getEngine() {
        return engine;
    }

    public void onAddedToEngine(Engine engine){}

    public void update(float dt){}

    final void addToEngine(Engine engine) {
        this.engine = engine;
        onAddedToEngine(engine);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    final void removeFromEngine(){
        unsubscribeAll();
        Engine oldEngine = this.engine;
        engine = null;
        onRemovedFromEngine(oldEngine);
    }

    public void onRemovedFromEngine(Engine e){}


    //*****************//
    //* SUBSCRIPTIONS *//
    //*****************//


    /**
     * Подписывается на ивенты движка (Возможно только после того как система была добавлена в движок)
     * Автоматически отписывается когда EntitySystem удаляется из движка
     */
    protected final<T> void subscribe(Subscription<T> subscription){
        if (subscriptions == null){
            subscriptions = new Array<Subscription>(5);
        }
        subscriptions.add(subscription);
        engine.subscribe(subscription);
    }

    /**
     * Подписывается на ивенты движка (Возможно только после того как система была добавлена в движок)
     * Автоматически отписывается когда EntitySystem удаляется из движка
     */
    protected final<T> Subscription<T> subscribe(Class<T> eventClass, Listener<T> listener){
        CompositSubscription<T> subscription = new CompositSubscription<T>(eventClass, listener);
        subscribe(subscription);
        return subscription;
    }

    protected final void unsubscribe(Subscription subscription){
        engine.unsubscribe(subscription);
    }

    protected final void unsubscribeAll() {
        if (subscriptions != null){
            for (Subscription subscription : subscriptions) {
                engine.unsubscribe(subscription);
            }
            subscriptions = null;
        }
    }
}
