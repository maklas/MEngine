package ru.maklas.mengine.systems;

import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;

public class CollisionEntitySystem extends EntitySystem {

    public CollisionEntitySystem() {
        super(Engine.COLLISION_SYSTEM_PRIORITY);
    }


}
