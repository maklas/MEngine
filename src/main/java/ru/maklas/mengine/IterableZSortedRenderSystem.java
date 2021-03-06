package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Sort;
import ru.maklas.mengine.components.IRenderComponent;

import java.util.Comparator;

/**
 * <p>
 *     Automatically sorts Entities by their Layer.
 *     All is left is:
 *     <li>Create rendering component that extends {@link IRenderComponent}</li>
 *     <li>Create your own rendering implementation by extending this class</li>
 * <p>Use {@link #invalidate()} or {@link Engine#invalidateRender()} to re-sort Entities before next rendering</p>
 * </p>
 */
public abstract class IterableZSortedRenderSystem<T extends IRenderComponent> extends RenderEntitySystem{

    private final Class<T> componentClass;
    private final ComponentMapper<T> mapper;
    private ImmutableArray<Entity> entities;
    private final Array<Entity> sortedEntities = new Array<Entity>(true, 64);
    private static final Comparator<Entity> layerComparator = new Comparator<Entity>() {
        @Override
        public int compare(Entity e1, Entity e2) {
            return e1.layer - e2.layer;
        }
    };
    private final boolean invalidateOnEntityAdd;
    private final EntityListener listener;

    public IterableZSortedRenderSystem(Class<T> componentClass, boolean invalidateOnEntityAdd) {
        super();
        this.componentClass = componentClass;
        this.mapper = ComponentMapper.of(componentClass);
        this.invalidateOnEntityAdd = invalidateOnEntityAdd;
        listener = new EntityListener() {
            @Override
            public void entityAdded(Entity e) {
                invalidate();
            }

            @Override
            public void entityRemoved(Entity e) {
                sortedEntities.removeValue(e, true);
            }
        };
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        entities = engine.entitiesFor(componentClass);
        if (invalidateOnEntityAdd){
            engine.addListener(listener);
        }
    }

    @Override
    public void onRemovedFromEngine(Engine engine) {
        super.onRemovedFromEngine(engine);
        entities = null;
        sortedEntities.clear();
        if (invalidateOnEntityAdd){
            engine.removeListener(listener);
        }
    }

    @Override
    @SuppressWarnings("all")
    public final void render() {
        Array<Entity> sortedEntities = this.sortedEntities;
        final int mapperKey = this.mapper.id;

        if (!isValid()){
            sort();
            setValid();
        }

        renderStarted();

        for (Entity e : sortedEntities) {
            renderEntity(e, (T) e.get(mapperKey));
        }

        renderFinished();
    }

    private void sort() {
        Array<Entity> sortedEntities = this.sortedEntities;
        int size = entities.size();
        sortedEntities.setSize(size);
        System.arraycopy(entities.items(), 0, sortedEntities.items, 0, size);
        Sort.instance().sort(sortedEntities.items, layerComparator,  0, size);
    }

    protected abstract void renderStarted();

    protected abstract void renderEntity(Entity entity, T rc);

    protected abstract void renderFinished();
}
