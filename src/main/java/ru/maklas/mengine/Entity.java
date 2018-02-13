package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.utils.Signal;

import java.util.Arrays;

public class Entity {

    private static final Pool<EntityComponentEvent> eventPool = new Pool<EntityComponentEvent>() {
        @Override
        protected EntityComponentEvent newObject() {
            return new EntityComponentEvent();
        }
    };

    public int id = -1;
    public float x;
    public float y;
    public int zOrder;

    public final Signal<EntityComponentEvent> componentSignal = new Signal<EntityComponentEvent>();
    private Component[] components;
    Array<Component> componentArray = new Array<Component>(5);

    private Engine engine;
    boolean scheduledForRemoval = false;


    public Entity(float x, float y, int zOrder) {
        this.x = x;
        this.y = y;
        this.zOrder = zOrder;
        components = new Component[Engine.TOTAL_COMPONENTS];
    }

    public Entity() {
        components = new Component[Engine.TOTAL_COMPONENTS];
    }

    public Entity(int id) {
        this.id = id;
        components = new Component[ComponentMapper.counter];
    }

    public Entity add(Component component){
        ComponentMapper mapper = ComponentMapper.of(component.getClass());
        Component oldComponent = components[mapper.id];
        if (oldComponent != null){
            componentArray.removeValue(oldComponent, true);
        }
        components[mapper.id] = component;
        componentArray.add(component);
        componentSignal.dispatch(eventPool.obtain().setUp(this, component, mapper, true));
        return this;
    }

    public Component remove(Component component){
        return remove(component.getClass());
    }

    public Component remove(Class<? extends Component> clazz){
        ComponentMapper mapper = ComponentMapper.of(clazz);
        Component component = components[mapper.id];
        components[mapper.id] = null;
        if (component != null) {
            componentSignal.dispatch(eventPool.obtain().setUp(this, component, mapper, false));
            componentArray.removeValue(component, true);
        }
        return component;
    }

    @SuppressWarnings("all")
    public <T extends Component> T get(ComponentMapper<T> mapper){
        if (mapper.id >= components.length){
            return null;
        }
        return (T) components[mapper.id];
    }

    public final boolean isInEngine(){
        return engine != null;
    }

    void addToEngine(Engine engine){
        this.engine = engine;
    }

    protected void addedToEngine(Engine engine){}

    public Engine getEngine() {
        return engine;
    }

    void removeFromEngine(){
        removedFromEngine(engine);
        this.engine = null;
    }

    protected void removedFromEngine(Engine engine){}


    /** @return true if the entity is scheduled to be removed */
    public boolean isScheduledForRemoval () {
        return scheduledForRemoval;
    }

}
