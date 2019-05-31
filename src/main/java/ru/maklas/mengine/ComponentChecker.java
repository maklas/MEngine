package ru.maklas.mengine;

import com.badlogic.gdx.utils.Predicate;

/** Efficiently checks if Entity has all the components that this Checker supposed to check **/
public class ComponentChecker implements Predicate<Entity> {

    int[] types;

    ComponentChecker(int[] types) {
        this.types = types;
    }

    /** Checks if this Entity possesses all the components of this checker **/
    @Override
    public boolean evaluate(Entity e){
        Component[] c = e.components;
        for (int type : types) {
            if (c[type] == null) return false;
        }
        return true;
    }
}
