package ru.maklas.mengine;

/** Listens to {@link Engine#add(Entity)} and {@link Engine#remove(Entity)} methods to notify if Entities were added or removed **/
public interface EntityListener {

    /** Triggers when Entity was added to Engine **/
    void entityAdded(Entity e);

    /** Triggers when Entity was removed from Engine **/
    void entityRemoved(Entity e);

}
