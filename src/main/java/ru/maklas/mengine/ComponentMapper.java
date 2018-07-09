package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Maps components of Entities for really fast access
 */
public class ComponentMapper<T extends Component> {

    private static ObjectMap<Class<? extends Component>, ComponentMapper> assignedComponentTypes = new ObjectMap<Class<? extends Component>, ComponentMapper>();
    private static int counter;

    final int id;
    final Class<T> clazz;

    ComponentMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.id = counter++;
    }

    public static <C extends Component> ComponentMapper<C> of(Class<C> cClass){
        ComponentMapper<C> componentMapper = assignedComponentTypes.get(cClass);
        if (componentMapper == null){
            componentMapper = new ComponentMapper<C>(cClass);
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
