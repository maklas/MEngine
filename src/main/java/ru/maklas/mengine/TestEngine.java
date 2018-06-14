package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.performance_new.PerformanceAccumulator;
import ru.maklas.mengine.performance_new.results.PerformanceResult;

public class TestEngine extends Engine {

    PerformanceAccumulator accumulator;

    public TestEngine() {
        super();
        accumulator = new PerformanceAccumulator();
    }

    @Override
    public Engine add(@NotNull Entity entity) {
        accumulator.startedAddingEntity();
        Engine add = super.add(entity);
        accumulator.finishedAddingEntity();
        return add;
    }

    @Override
    public boolean remove(@NotNull Entity entity) {
        accumulator.startedRemovingEntity();
        boolean remove = super.remove(entity);
        accumulator.finishedRemovingEntity();
        return remove;
    }

    @Nullable
    @Override
    public Entity findById(int id) {
        accumulator.findByIdStarted(id);
        Entity byId = super.findById(id);
        accumulator.findByIdFinished();
        return byId;
    }

    @Override
    public void dispatch(Object event) {
        accumulator.eventDispatchStarted(event);
        super.dispatch(event);
        accumulator.eventDispatchFinished();
    }

    @Override
    void processAfterUpdateOperations() {
        accumulator.afterUpdateStarted();
        super.processAfterUpdateOperations();
        accumulator.afterUpdateFinished();
    }

    @Override
    public void update(float dt) {
        if(this.updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        updating = true;

        Array<EntitySystem> systems = systemManager.getSystems();
        PerformanceAccumulator acc = this.accumulator;
        acc.updateStarted();

        for (EntitySystem system : systems) {
            acc.systemStarted(system);
            if (system.isEnabled()) {
                system.update(dt);
            }
            acc.systemFinished();
            if (inUpdateDirty){
                processInUpdateOperations();
                acc.laterExecutionFinished(true);
            } else acc.laterExecutionFinished(false);
        }

        updating = false;
        processAfterUpdateOperations();

        acc.updateFinished();
    }

    @Override
    public void render() {

        RenderEntitySystem renderSystem = systemManager.getRenderSystem();
        if (renderSystem != null && renderSystem.isEnabled()){
            accumulator.renderStarted(renderSystem);
            renderSystem.render();
            accumulator.renderFinished();
        }
    }

    public PerformanceResult captureResults() {
        return accumulator.captureResults();
    }
}
