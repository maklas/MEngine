package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;
import ru.maklas.mengine.components.IRenderComponent;
import ru.maklas.mengine.utils.ImmutableArray;

import java.util.Comparator;

/**
 * <p>
 *     Automatically sorts Entities by their ZOder.
 *     All is left is:
 *     <li>Create rendering component that extends {@link IRenderComponent}</li>
 *     <li>Create your own rendering implementation by extending this class</li>
 * <p>Use {@link #invalidate()} or {@link Engine#invalidateRenderZ()} to re-sort Entities on next render.</p>
 * </p>
 */
public abstract class IterableZSortedRenderSystem<T extends IRenderComponent> extends RenderEntitySystem implements EntityListener {

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

    @Override
    public void onRemovedFromEngine(Engine e) {
        super.onRemovedFromEngine(e);
        entities = null;
        sortedEntities.clear();
    }

    @Override
    @SuppressWarnings("all")
    public final void render() {
        Array<Entity> sortedEntities = this.sortedEntities;
        final int mapperKey = this.mapper.id;

        if (!isValid()){
            sort();
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
        Sort.instance().sort(sortedEntities.items, zOrderComparator,  0, size);
    }

    protected abstract void renderStarted();

    protected abstract void renderEntity(Entity entity, T rc);

    protected abstract void renderFinished();

    @Override
    public final void entityAdded(Entity e) {
        invalidate();
    }

    @Override
    public final void entityRemoved(Entity e) {
        invalidate();
    }
}
