package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;

public class SubscriptionSystem {

    protected Engine engine;
    Array<Subscription> subscriptions;
    Array<Disposable> disposables;


    final void addToEngine(Engine engine) {
        this.engine = engine;
        onAddedToEngine(engine);
    }

    final void removeFromEngine(){
        unsubscribeAll();
        Engine oldEngine = this.engine;
        engine = null;
        onRemovedFromEngine(oldEngine);
        disposeAll();
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
     * Subscribes to engine events. (Can be possible only after this system was added to engine)
     * Automatically unsubscribes when the system is removed from engine
     */
    protected final <T> void subscribe(Subscription<T> subscription){
        if (subscriptions == null){
            subscriptions = new Array<Subscription>(5);
        }
        subscriptions.add(subscription);
        engine.subscribe(subscription);
    }

    /**
     * Subscribes to engine events. (Can be possible only after this system was added to engine)
     * Automatically unsubscribes when the system is removed from engine
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

    /**
     * Remembers disposable. It will be automatically disposed after system is removed from engine.
     * Triggered after {@link #onRemovedFromEngine(Engine)}, so it's still safe to use in this method.
     * Useful if you don't want to keep track of Disposable objects when system is active.
     */
    protected final <T extends Disposable> T addDisposable(T disposable){
        if (disposables == null){
            disposables = new Array<Disposable>(5);
        }
        disposables.add(disposable);
        return disposable;
    }

    /**
     * Disposes and forgets all disposables which were added with {@link #addDisposable(Disposable)}.
     * Warning! Called automatically when this system is removed from engine!
     */
    protected final void disposeAll() {
        if (disposables != null){
            for (Disposable disposable : disposables) {
                disposable.dispose();
            }
            disposables = null;
        }
    }
}
