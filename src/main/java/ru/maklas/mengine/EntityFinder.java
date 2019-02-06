package ru.maklas.mengine;

/**
 * Interface used for finding Entities in engine knowing only their Ids.
 * You can use existing implementations or create your own.
 */
public interface EntityFinder {

    void onAddedToEngine(Engine engine);

    Entity find(Engine engine, int id);

}
