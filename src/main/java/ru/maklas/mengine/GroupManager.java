package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;

public class GroupManager {

    private final Engine engine;
    private Group[] groups = new Group[Engine.INITIAL_COMPONENTS];

    public GroupManager(Engine engine) {
        this.engine = engine;
    }


    void componentAdded(Entity target, ComponentMapper<? extends Component> mapper){
        assertGroupSize(mapper.id);
        Group group = groups[mapper.id];
        if (group != null) {
            group.add(target);
        }
    }

    void componentRemoved(Entity target, ComponentMapper<? extends Component> mapper){
        if (groups.length <= mapper.id) return;
        Group group = groups[mapper.id];
        if (group != null){
            group.remove(target);
        }
    }

    void entityAdded(Entity entity){
        Array<Component> componentArray = entity.componentArray;
        for (Component component : componentArray) {
            componentAdded(entity, ComponentMapper.of(component.getClass()));
        }
    }

    void entityRemoved(Entity entity){
        Array<Component> componentArray = entity.componentArray;
        for (Component component : componentArray) {
            componentRemoved(entity, ComponentMapper.of(component.getClass()));
        }
    }

    public Group of(ComponentMapper mapper){
        assertGroupSize(mapper.id);
        Group group = groups[mapper.id];
        if (group == null){
            group = new Group();
            groups[mapper.id] = group;

            ImmutableArray<Entity> entities = engine.getEntities();
            int size = entities.size();
            for (int i = 0; i < size; i++) {
                Component component = entities.get(i).get(mapper);
                if (component != null){
                    componentAdded(entities.get(i), mapper);
                }
            }


        }
        return group;
    }

    private void assertGroupSize(int size){
        if (groups.length <= size){
            Group[] newGroups = new Group[size + 1];
            System.arraycopy(groups, 0, newGroups, 0, groups.length);
            groups = newGroups;
        }
    }

    public Group of(Class<? extends Component> clazz){
        return of(ComponentMapper.of(clazz));
    }

    public void clearAll() {
        for (Group group : groups) {
            if (group != null){
                group.entityArray.clear();
            }
        }

    }
}
