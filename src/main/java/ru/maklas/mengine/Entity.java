package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.utils.Signal;

public class Entity {

    private static final Pool<EntityComponentEvent> eventPool = new Pool<EntityComponentEvent>() {
        @Override
        protected EntityComponentEvent newObject() {
            return new EntityComponentEvent();
        }
    };

    public static AngleNormalizer angleNormalizer = new AngleNormalizer360();

    public int id = -1;
    public float x;
    public float y;
    public int type = -1;
    float angle;
    public int zOrder;

    public final Signal<EntityComponentEvent> componentSignal = new Signal<EntityComponentEvent>();
    private final Component[] components;
    Array<Component> componentArray = new Array<Component>(12);
    Array<Subscription> subscriptions;

    private Engine engine;


    public Entity() {
        this(-1, 0, 0, 0);
    }

    public Entity(int id) {
        this(id, 0, 0, 0);
    }

    public Entity(float x, float y, int zOrder) {
        this(-1, x, y, zOrder);
    }

    public Entity(int id, float x, float y, int zOrder) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.zOrder = zOrder;
        components = new Component[Engine.TOTAL_COMPONENTS];
    }


    // COMPONENT MANIPULATION

    public final Entity add(Component component){
        Class aClass = component.getClass();
        return add(component, ComponentMapper.of(aClass));
    }

    public final <T extends Component> Entity add(T component, ComponentMapper<T> mapper){
        Component oldComponent = components[mapper.id];
        if (oldComponent != null){
            componentArray.removeValue(oldComponent, true);
            EntityComponentEvent e = eventPool.obtain().setUp(this, oldComponent, mapper, false);
            componentSignal.dispatch(e);
            eventPool.free(e);
        }
        components[mapper.id] = component;
        componentArray.add(component);
        EntityComponentEvent e = eventPool.obtain().setUp(this, component, mapper, true);
        componentSignal.dispatch(e);
        eventPool.free(e);
        return this;
    }

    public final Component remove(Class<? extends Component> cClass){
        return remove(ComponentMapper.of(cClass));
    }

    public final Component remove(ComponentMapper mapper){
        Component component = components[mapper.id];
        components[mapper.id] = null;
        if (component != null) {
            componentArray.removeValue(component, true);
            EntityComponentEvent e = eventPool.obtain().setUp(this, component, mapper, false);
            componentSignal.dispatch(e);
            eventPool.free(e);
        }
        return component;
    }

    @SuppressWarnings("all")
    public final <T extends Component> T get(ComponentMapper<T> mapper){
        return (T) components[mapper.id];
    }

    @SuppressWarnings("all")
    final Component get(int mapperKey){
        return components[mapperKey];
    }

    @SuppressWarnings("all")
    public final <T extends Component> T get(Class<T> cClass){
        return (T) components[ComponentMapper.of(cClass).id];
    }



    //GETTERS/SETTERS

    public final float getAngle() {
        return angle;
    }

    public final void setAngle(float angle) {
        this.angle = angleNormalizer.normalize(angle);
    }


    //*****************//
    //* SUBSCRIPTIONS *//
    //*****************//


    /**
     * Подписывается на ивенты движка (Возможно только после того как Entity был добавлен в движок)
     * Автоматически отписывается когда Enitity удаляется из движка
     */
    protected final<T> void subscribe(Subscription<T> subscription){
        if (subscriptions == null){
            subscriptions = new Array<Subscription>(5);
        }
        subscriptions.add(subscription);
        engine.subscribe(subscription);
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

    public final boolean isInEngine(){
        return engine != null;
    }

    protected void addedToEngine(Engine engine){}

    public final Engine getEngine() {
        return engine;
    }

    protected void removedFromEngine(Engine engine){}

    public final void rotate(float angle) {
        this.angle = angleNormalizer.normalize(this.angle + angle);
    }


    //STRINGS

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", zOrder=" + zOrder +
                '}';
    }

    public final String toStringWithComponents(){
        return "Entity = {" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", zOrder=" + zOrder +
                ", Components=" + componentArray.toString() +
                '}';
    }
}
