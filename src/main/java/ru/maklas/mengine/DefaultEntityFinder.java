package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation. Iterates over Entities and compares by Id. Most stable, least efficient.
 */
public class DefaultEntityFinder implements EntityFinder {

    Array.ArrayIterator<Entity> entityIterator;

    @Override
    public void onAddedToEngine(Engine engine) {
        entityIterator = new Array.ArrayIterator<Entity>(engine.entities);
    }

    @Nullable
    @Override
    public Entity find(Engine engine, int id) {
        Array.ArrayIterator<Entity> iterator = entityIterator;
        iterator.reset();
        while (iterator.hasNext()){
            Entity next = iterator.next();
            if (next.id == id){
                return next;
            }
        }
        return null;
    }
}
