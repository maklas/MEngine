package ru.maklas.mengine;

import com.badlogic.gdx.utils.ObjectMap;

public class ComponentMapper<T extends Component> {

    static int counter;
    int id;


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


    public T get(Entity entity){
        return entity.get(this);
    }



}
