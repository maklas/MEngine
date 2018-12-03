package ru.maklas.mengine;

public interface EntityFinder {

    void onAddedToEngine(Engine engine);

    Entity find(Engine engine, int id);

}
