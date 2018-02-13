package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.utils.ImmutableArray;

public class Engine {

    private final Array<Entity> entities;
    private final ImmutableArray<Entity> immutableEntities;

    Engine() {
        entities = new Array<Entity>(50);
        immutableEntities = new ImmutableArray<Entity>(entities);
    }

    public void add(Entity entity){

    }



    public ImmutableArray<Entity> getEntities(){
        return immutableEntities;
    }


    public class Builder{

        Array<Class<? extends Component>> registeredComponents;

        public Builder registerComponent(Class<? extends Component> componentClass){
            registeredComponents.add(componentClass);
            return this;
        }

        public Engine build(){
            return new Engine();
        }

    }

}
