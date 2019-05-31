package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.utils.AngleNormalizerNONE;
import ru.maklas.mengine.utils.Listener;
import ru.maklas.mengine.utils.Signal;

public class Entity {

    //TODO fix memory leak and make Engine be able to run in multiple threads
    private static final Pool<EntityComponentEvent> eventPool = new Pool<EntityComponentEvent>() {
        @Override
        protected EntityComponentEvent newObject() {
            return new EntityComponentEvent();
        }
    };

    public static AngleNormalizer angleNormalizer = new AngleNormalizerNONE();

    /** Id of the entity. Use it as you wish. MEngine's only interaction with it is via {@link Engine#findById(int)} **/
    public int id;
    /** X position for Entity **/
    public float x;
    /** Y position for Entity **/
    public float y;
    /**
     * Angle of the Entity. Field is private, so use getter and setter.
     * {@link AngleNormalizer} is used for keeping angle in specific region 0..360 for example
     */
    private float angle;
    /**
     * Position of the Entity in Z axis.
     * {@link IterableZSortedRenderSystem} uses this parameter to render Entities in order.
     */
    public int layer;
    /**
     * User-defined int. Can be used as a mask or for fast Entity classification. Basically for all you want.
     * During game development with ECS, there is always a need to make EntityTypeComponent that will tell what kind
     * of Entity it is. With this int, type can be accessed much faster.
     */
    public int type;


    public final Signal<EntityComponentEvent> componentSignal = new Signal<EntityComponentEvent>(2);
    final Component[] components = new Component[Engine.TOTAL_COMPONENTS];
    private Array<Subscription> subscriptions;
    Engine engine;
    Array<Component> componentArray = new Array<Component>(12);

    public Entity() {}

    public Entity(int id) {
        this.id = id;
    }

    public Entity(float x, float y, int layer) {
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public Entity(int id, float x, float y, int layer) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    public Entity(int id, int type, float x, float y, int layer) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.layer = layer;
    }

    // COMPONENT MANIPULATION

    /** Never remove from this array **/
    public final Array<Component> getComponents() {
        return componentArray;
    }

    /**
     * Adds new Component to the entity. Replaces if this Entity already had component of this class.
     *
     */
    public final Entity add(Component component){
        Class aClass = component.getClass();
        return add(component, ComponentMapper.of(aClass));
    }

    /**
     * Adds new Component to the entity with the help of Component Mapper.
     * A bit faster than {@link #add(Component)} (can be used for microOptimization).
     * Replaces if this Entity already had component of this class.
     */
    public final <T extends Component> Entity add(T component, ComponentMapper<T> mapper){
        int mapperId = mapper.id;
        Component oldComponent = components[mapperId];
        if (oldComponent != null){
            componentArray.removeValue(oldComponent, true);
            EntityComponentEvent e = eventPool.obtain().setUp(this, oldComponent, mapperId, false);
            componentSignal.dispatch(e);
            eventPool.free(e);
        }
        components[mapperId] = component;
        componentArray.add(component);
        EntityComponentEvent e = eventPool.obtain().setUp(this, component, mapperId, true);
        componentSignal.dispatch(e);
        eventPool.free(e);
        return this;
    }

    /** Removes Component of specified ComponentMapper from this entity **/
    public final <T extends Component> T remove(ComponentMapper<T> mapper){
        int mapperId = mapper.id;
        Component component = components[mapperId];
        components[mapperId] = null;
        if (component != null) {
            componentArray.removeValue(component, true);
            EntityComponentEvent e = eventPool.obtain().setUp(this, component, mapperId, false);
            componentSignal.dispatch(e);
            eventPool.free(e);
        }
        return (T) component;
    }

    /**
     * Gets component of specified class from this Entity. Since uses Map for retrieval, complexity for the worst case is O(log(n).
     * Use {@link #get(ComponentMapper)} for ligtning O(1) speed.
     */
    @SuppressWarnings("all")
    public final <T extends Component> T get(Class<T> cClass){
        return (T) components[ComponentMapper.of(cClass).id];
    }

    /** Fastest way to get a component from Entity **/
    @SuppressWarnings("all")
    public final <T extends Component> T get(ComponentMapper<T> mapper){
        return (T) components[mapper.id];
    }

    @SuppressWarnings("all")
    final Component get(int mapperKey){
        return components[mapperKey];
    }



    //GETTERS/SETTERS

    /** @return angle of this Entity **/
    public final float getAngle() {
        return angle;
    }

    /** Sets angle for this Entity **/
    public final void setAngle(float angle) {
        this.angle = angleNormalizer.normalize(angle);
    }


    public Entity set(float x, float y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Entity set(float x, float y, int layer){
        this.x = x;
        this.y = y;
        this.layer = layer;
        return this;
    }

    public Entity set(int id, int type, float x, float y, int layer){
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.layer = layer;
        return this;
    }

    //*****************//
    //* SUBSCRIPTIONS *//
    //*****************//


    /**
     * Subscribes for Events directly through Engine (Only possible after Entity was added to engine, so please use it during {@link #addedToEngine(Engine)} method call).
     * Automatically removes subscription when Entity is removed from Engine!
     */
    protected final<T> void subscribe(Subscription<T> subscription){
        if (subscriptions == null){
            subscriptions = new Array<Subscription>(5);
        }
        subscriptions.add(subscription);
        engine.subscribe(subscription);
    }


    /**
     * Same as {@link #subscribe(Subscription)}, but more suitable for lambdas and method references.
     * Subscribes for Events directly through Engine (Only possible after Entity was added to engine, so please use it during {@link #addedToEngine(Engine)} method call).
     * Automatically removes subscription when Entity is removed from Engine!
     * @return Subscription that was formed.
     */
    protected final <T> Subscription<T> subscribe(Class<T> eventClass, Listener<T> listener){
        CompositSubscription<T> subscription = new CompositSubscription<T>(eventClass, listener);
        subscribe(subscription);
        return subscription;
    }

    /** Unsubscribes for specific subscription directly from current Engine (make sure Entity is in Engine before calling it) **/
    protected final void unsubscribe(Subscription subscription){
        engine.unsubscribe(subscription);
    }

    /** Unsubscribes from all Subscriptions directly from current Engine (make sure Entity is in Engine before calling it) **/
    protected final void unsubscribeAll() {
        if (subscriptions != null){
            for (Subscription subscription : subscriptions) {
                engine.unsubscribe(subscription);
            }
            subscriptions = null;
        }
    }

    //ENGINE events
    final void addToEngine(Engine engine){
        this.engine = engine;
        addedToEngine(engine);
    }

    final void removeFromEngine(){
        Engine engine = this.engine;
        unsubscribeAll();
        this.engine = null;
        removedFromEngine(engine);
    }

    /** Check if this entity is inside of Engine **/
    public final boolean isInEngine(){
        return engine != null;
    }

    /** Triggers when this Entity is added to Engine **/
    protected void addedToEngine(Engine engine){}

    /** Gets Engine that this Entity was added into. Might be null if this Entity is not in Engine atm **/
    public final Engine getEngine() {
        return engine;
    }

    /** Triggers when this Entity is removed from Engine **/
    protected void removedFromEngine(Engine engine){}

    /** Rotates this Entity by specified angle **/
    public final void rotate(float angle) {
        this.angle = angleNormalizer.normalize(this.angle + angle);
    }


    //STRINGS

    /** Basic info about this Entity. Use {@link #toStringWithComponents()} to get full data **/
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                ", layer=" + layer +
                ", inEngine=" + isInEngine() +
                ", Components=" + componentArray.size +
                '}';
    }

    /** Full info about this Entity. Don't forget to specify toString() for each component for this method to be of any Use **/
    public final String toStringWithComponents(){
        return "Entity = {" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                ", layer=" + layer +
                ", inEngine=" + isInEngine() +
                ", Components=" + componentArray.toString() +
                '}';
    }
}
