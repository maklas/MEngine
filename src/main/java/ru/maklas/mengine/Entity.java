package ru.maklas.mengine;

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

    private Engine engine;
    boolean scheduledForRemoval = false;


    public Entity(float x, float y, int zOrder) {
        this.x = x;
        this.y = y;
        this.zOrder = zOrder;
        components = new Component[ComponentMapper.counter];
    }

    public Entity() {
        components = new Component[ComponentMapper.counter];
    }

    public Entity(int id) {
        this.id = id;
        components = new Component[ComponentMapper.counter];
    }

    public Entity add(Component component){
        ComponentMapper mapper = ComponentMapper.of(component.getClass());
        if (mapper.id >= components.length){
            expandComponentsArray(mapper.id);
        }
        components[mapper.id] = component;

        componentSignal.dispatch(eventPool.obtain().setUp(this, component, true));
        return this;
    }

    private void expandComponentsArray(int id) {
        components = Arrays.copyOf(components, id + 1);
    }

    public Component remove(Component component){
        ComponentMapper mapper = ComponentMapper.of(component.getClass());
        Component[] components = this.components;
        if (mapper.id >= components.length){
            return null;
        }

        Component oldC = components[mapper.id];
        if (oldC == component){
            return null;
        }

        components[mapper.id] = component;
        componentSignal.dispatch(eventPool.obtain().setUp(this, component, false));
        return oldC;
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
