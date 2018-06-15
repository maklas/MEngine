package ru.maklas.mengine.performance_new;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.performance_new.captures.EntityCapture;
import ru.maklas.mengine.performance_new.captures.EventCapture;
import ru.maklas.mengine.performance_new.captures.FindByIDCapture;
import ru.maklas.mengine.performance_new.captures.SystemCapture;

public class FrameData implements Pool.Poolable {

    public long engineUpdateTime;
    public long renderTime;
    public long afterUpdateTime;
    public Array<SystemCapture>   systems = new Array<SystemCapture>();
    public Array<EventCapture>    events = new Array<EventCapture>();
    public Array<FindByIDCapture> finds = new Array<FindByIDCapture>();
    public Array<EntityCapture>   entities = new Array<EntityCapture>();

    private Pool<SystemCapture> systemPool = new Pool<SystemCapture>() {
        @Override
        protected SystemCapture newObject() {
            return new SystemCapture();
        }
    };

    private Pool<EventCapture> eventPool = new Pool<EventCapture>() {
        @Override
        protected EventCapture newObject() {
            return new EventCapture();
        }
    };

    private Pool<FindByIDCapture> findPool = new Pool<FindByIDCapture>() {
        @Override
        protected FindByIDCapture newObject() {
            return new FindByIDCapture();
        }
    };

    private Pool<EntityCapture> entityPool = new Pool<EntityCapture>() {
        @Override
        protected EntityCapture newObject() {
            return new EntityCapture();
        }
    };

    @Override
    public void reset() {
        renderTime = 0;

        systemPool.freeAll(systems);
        systems.clear();

        eventPool.freeAll(events);
        events.clear();

        findPool.freeAll(finds);
        finds.clear();

        entityPool.freeAll(entities);
        entities.clear();
    }

    public void addSystemUpdate(Class<? extends EntitySystem> systemClass, long updateTime, long laterExecutionTime) {
        systems.add(systemPool.obtain().init(systemClass, updateTime, laterExecutionTime));
    }

    public void addSystemUpdate(Class<? extends EntitySystem> systemClass, long updateTime) {
        systems.add(systemPool.obtain().init(systemClass, updateTime, 0));
    }

    public void eventDispatch(EventCapture eventCapture) {
        events.add(eventPool.obtain().from(eventCapture));
    }

    public void findById(int id, long time, @Nullable EntitySystem currentSystem) {
        finds.add(findPool.obtain().init(id, time, currentSystem == null ? null : currentSystem.getClass()));
    }

    public void entityAdd(long time) {
        entities.add(entityPool.obtain().init(true, time));
    }

    public void entityRemove(long time) {
        entities.add(entityPool.obtain().init(false, time));
    }
}
