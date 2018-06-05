package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Maps components of Entities for really fast access
 */
public class ComponentMapper<T extends Component> {

    static int counter;
    final int id;


    public ComponentMapper() {
        this.id = counter++;
    }

    private static ObjectMap<Class<? extends Component>, ComponentMapper> assignedComponentTypes = new ObjectMap<Class<? extends Component>, ComponentMapper>();

    public static <C extends Component> ComponentMapper<C> of(Class<C> cClass){
        ComponentMapper<C> componentMapper = assignedComponentTypes.get(cClass);
        if (componentMapper == null){
            componentMapper = new ComponentMapper<C>();
            assignedComponentTypes.put(cClass, componentMapper);
        }
        return componentMapper;
    }

    public static int totalComponentMappersCreated(){
        return counter;
    }

    public T get(Entity entity){
        return entity.get(this);
    }



}
