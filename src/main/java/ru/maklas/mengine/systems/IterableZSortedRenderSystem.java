package ru.maklas.mengine.systems;

import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.components.IRenderComponent;
import ru.maklas.mengine.utils.ImmutableArray;

import java.util.Comparator;

public abstract class IterableZSortedRenderSystem<T extends IRenderComponent> extends RenderEntitySystem {

    private final Class<T> componentClass;
    private final ComponentMapper<T> mapper;
    private ImmutableArray<Entity> entities;
    private final Array<Entity> sortedEntities = new Array<Entity>();
    private static final Comparator<Entity> zOrderComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {
            return e1.zOrder - e2.zOrder;
        }
    };

    public IterableZSortedRenderSystem(Class<T> componentClass) {
        this.componentClass = componentClass;
        this.mapper = ComponentMapper.of(componentClass);
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        entities = engine.entitiesFor(componentClass);
    }

    public ImmutableArray<Entity> getEntities() {
        return entities;
    }

    @Override
    public final void render() {
        Array<Entity> sortedEntities = this.sortedEntities;
        ImmutableArray<Entity> entities = this.entities;
        ComponentMapper<T> mapper = this.mapper;


        sortedEntities.clear();
        for (Entity entity : entities) {
            sortedEntities.add(entity);
        }
        sortedEntities.sort(zOrderComparator);

        renderStarted();

        for (Entity e : sortedEntities) {
            renderEntity(e, e.get(mapper));
        }

        renderFinished();
    }

    protected abstract void renderStarted();

    protected abstract void renderEntity(Entity entity, T rc);

    protected abstract void renderFinished();
}
