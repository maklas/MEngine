package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;

/** A group of Entities all of which have specific component in them **/
public class Group {

    Array<Entity> entityArray = new Array<Entity>();
    ImmutableArray<Entity> immutables = new ImmutableArray<Entity>(entityArray);

    void add(Entity target) {
        entityArray.add(target);
    }

    void remove(Entity target) {
        entityArray.removeValue(target, true);
    }

    public ImmutableArray<Entity> getEntities() {
        return immutables;
    }
}
