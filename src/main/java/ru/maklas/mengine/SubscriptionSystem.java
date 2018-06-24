package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;

public class SubscriptionSystem {

    protected Engine engine;
    protected Array<Subscription> subscriptions;


    final void addToEngine(Engine engine) {
        this.engine = engine;
        onAddedToEngine(engine);
    }


    final void removeFromEngine(){
        unsubscribeAll();
        Engine oldEngine = this.engine;
        engine = null;
        onRemovedFromEngine(oldEngine);
    }

    public void onAddedToEngine(Engine engine){}

    public void onRemovedFromEngine(Engine engine){}


    public final Engine getEngine() {
        return engine;
    }

    protected final ImmutableArray<Entity> entitiesFor(Class<? extends Component> componentClass){
        return engine.entitiesFor(componentClass);
    }

    /**
     * Throws AssertionError if system is not currently in the same engine.
     */
    protected final void assertSystemAdded(Class <? extends SubscriptionSystem> system){
        if (engine.getSystemManager().getSystem(system) == null)
            throw new AssertionError();
    }

    /**
     * Throws null pointer exception if system wasn't in engine during this call
     */
    protected final void dispatch(Object event){
        engine.dispatch(event);
    }

    /**
     * Подписывается на ивенты движка (Возможно только после того как система была добавлена в движок)
     * Автоматически отписывается когда EntitySystem удаляется из движка
     */
    protected final <T> void subscribe(Subscription<T> subscription){
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
    protected final <T> Subscription<T> subscribe(Class<T> eventClass, Listener<T> listener){
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
