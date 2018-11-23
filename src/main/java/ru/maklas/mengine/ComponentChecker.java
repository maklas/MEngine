package ru.maklas.mengine;

import com.badlogic.gdx.utils.Predicate;

public class ComponentChecker implements Predicate<Entity> {

    int[] types;

    public ComponentChecker(int[] types) {
        this.types = types;
    }

    /**
     * Checks if this Entity possesses all the components of this checker
     */
    @Override
    public boolean evaluate(Entity e){
        Component[] c = e.components;
        for (int type : types) {
            if (c[type] == null) return false;
        }
        return true;
    }
}
