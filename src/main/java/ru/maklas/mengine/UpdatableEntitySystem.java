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
    public void update(float dt) {
        Array<UpdatableEntity> updatableEntities = this.entities;
        for (UpdatableEntity ue : updatableEntities) {
            ue.update(dt);
        }
    }
}
