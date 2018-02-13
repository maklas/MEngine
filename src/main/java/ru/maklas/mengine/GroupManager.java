package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;

public class GroupManager {

    private Group[] groups = new Group[Engine.TOTAL_COMPONENTS];


    public void componentAdded(Entity target, ComponentMapper<? extends Component> mapper){
        Group group = groups[mapper.id];
        if (group == null){
            group = new Group();
            groups[mapper.id] = group;
        }

        group.add(target);
    }

    public void componentRemoved(Entity target, ComponentMapper<? extends Component> mapper){
        Group group = groups[mapper.id];
        if (group == null){
            group = new Group();
            groups[mapper.id] = group;
        }

        group.remove(target);
    }

    public void entityAdded(Entity entity){
        Array<Component> componentArray = entity.componentArray;
        for (Component component : componentArray) {
            componentAdded(entity, ComponentMapper.of(component.getClass()));
        }
    }

    public void entityRemoved(Entity entity){
        Array<Component> componentArray = entity.componentArray;
        for (Component component : componentArray) {
            componentRemoved(entity, ComponentMapper.of(component.getClass()));
        }

    }


    public Group of(ComponentMapper mapper){
        Group group = groups[mapper.id];
        if (group == null){
            group = new Group();
            groups[mapper.id] = group;
        }
        return group;
    }

    public Group of(Class<? extends Component> clazz){
        return of(ComponentMapper.of(clazz));
    }

}
