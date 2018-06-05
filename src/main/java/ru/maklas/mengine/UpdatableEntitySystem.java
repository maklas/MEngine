package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;

/**
 * System that update all {@link UpdatableEntity}.
 */
public class UpdatableEntitySystem extends EntitySystem {

    private Array<UpdatableEntity> entities;

    public UpdatableEntitySystem(int priority) {
        super(priority);
    }

    public UpdatableEntitySystem() {
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        entities = engine.updatableEntities;
    }

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        entities = null;
    }

    @Override
    public void update(float dt) {
        Array<UpdatableEntity> updatableEntities = this.entities;
        int size = updatableEntities.size;
        for (int i = 0; i < size; i++) {
            updatableEntities.get(i).update(dt);
        }
    }
}
