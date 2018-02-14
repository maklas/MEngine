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

    public static AngleNormalizator angleNormalizator = new AngleNormalizator360();

    public int id = -1;
    public float x;
    public float y;
    public int type = -1;
    float angle;
    public int zOrder;

    public final Signal<EntityComponentEvent> componentSignal = new Signal<EntityComponentEvent>();
    private final Component[] components;
    Array<Component> componentArray = new Array<Component>(5);

    private Engine engine;


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
        components = new Component[Engine.TOTAL_COMPONENTS];
    }


    // COMPONENT MANIPULATION

    public Entity add(Component component){
        Class aClass = component.getClass();
        return add(component, ComponentMapper.of(aClass));
    }

    public <T extends Component> Entity add(T component, ComponentMapper<T> mapper){
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

    public Component remove(Class<? extends Component> cClass){
        return remove(ComponentMapper.of(cClass));
    }

    public Component remove(ComponentMapper mapper){
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
    public <T extends Component> T get(ComponentMapper<T> mapper){
        return (T) components[mapper.id];
    }

    @SuppressWarnings("all")
    public <T extends Component> T get(Class<T> cClass){
        return (T) components[ComponentMapper.of(cClass).id];
    }



    //GETTERS/SETTERS

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angleNormalizator.normalize(angle);
    }

    //ENGINE events

    final void addToEngine(Engine engine){
        this.engine = engine;
    }

    final void removeFromEngine(){
        Engine engine = this.engine;
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


    //STRINGS

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", zOrder=" + zOrder +
                '}';
    }

    public String toStringWithComponents(){
        return "Entity = {" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", zOrder=" + zOrder +
                ", Components=" + componentArray.toString() +
                '}';
    }

    public void rotate(float angle) {
        this.angle = angleNormalizator.normalize(this.angle + angle);
    }
}
