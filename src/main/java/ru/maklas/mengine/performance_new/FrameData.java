package ru.maklas.mengine.performance_new;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.RenderEntitySystem;
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

    @Override
    public void reset() {
        renderTime = 0;
        systems.clear();
        events.clear();
        finds.clear();
        entities.clear();
    }

    public void addSystemUpdate(Class<? extends EntitySystem> systemClass, long updateTime, long laterExecutionTime) {
        systems.add(new SystemCapture(systemClass, updateTime, laterExecutionTime));
    }

    public void addSystemUpdate(Class<? extends EntitySystem> systemClass, long updateTime) {
        systems.add(new SystemCapture(systemClass, updateTime, 0));
    }

    public void eventDispatch(EventCapture eventCapture) {
        events.add(eventCapture);
    }

    public void findById(int id, long time, EntitySystem currentSystem) {
        finds.add(new FindByIDCapture(id, time, currentSystem.getClass()));
    }

    public void entityAdd(long time) {
        entities.add(new EntityCapture(true, time));
    }

    public void entityRemove(long time) {
        entities.add(new EntityCapture(false, time));
    }
}
