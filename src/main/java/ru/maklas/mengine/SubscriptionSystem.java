package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.utils.Listener;

/**
 * <p>
 *      Base System class. Does not provide update() method. Is able to subscribe to events and dispatch them through Engine.
 *      A System is a class that performs specific action on specific {@link Entity Entities} based on their {@link Component components}.
 * </p>
 * <p>
 * Examples of SubscriptionSystems:
 *      <li>SoundSystem</li>
 *      <li>SpecialEffectsSystem</li>
 *      <li>DamageSystem</li>
 *      <li>HealthSystem</li>
 * </p>
 * <p>
 *     These systems, if added to Engine, will act upon Entities whenever event fires.
 * </p>
 */
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

    /**
     * Called whenever this system is added to engine. By this time, system will have access to Engine.
     * It's a good place to subscribe to systems, create your objects, obtain anything you need from Engine.bundler.
     */
    public void onAddedToEngine(Engine engine){}

    /**
     * Called when this System is removed from Engine.
     * Good place to clean up, do some actions on Entities or remove components
     */
    public void onRemovedFromEngine(Engine engine){}


    public final Engine getEngine() {
        return engine;
    }

    /** @see Engine#entitiesFor(Class) **/
    protected final ImmutableArray<Entity> entitiesFor(Class<? extends Component> componentClass){
        return engine.entitiesFor(componentClass);
    }

    /** Throws AssertionError if system is not currently in the same engine **/
    protected final void assertSystemAdded(Class <? extends SubscriptionSystem> system){
        if (engine.getSystemManager().getSystem(system) == null)
            throw new AssertionError();
    }

    /** Throws null pointer exception if system wasn't in engine during this call **/
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

    /**
     * Unsubscribes from Event. Don't forget to keep your Subscription if you want to do it.
     * Is not necessary to be called if you want to unsubscribe whenever System is removed from Engine as it does so automatically.
     */
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
