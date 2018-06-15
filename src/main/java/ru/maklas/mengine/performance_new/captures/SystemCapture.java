package ru.maklas.mengine.performance_new.captures;

import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.EntitySystem;

public class SystemCapture implements Pool.Poolable{

    public Class<? extends EntitySystem> systemClass;
    public long updateTime;
    public long laterExecutionTime;

    public SystemCapture() {

    }

    public SystemCapture(Class<? extends EntitySystem> systemClass, long updateTime, long laterExecutionTime) {
        this.systemClass = systemClass;
        this.updateTime = updateTime;
        this.laterExecutionTime = laterExecutionTime;
    }

    public SystemCapture init(Class<? extends EntitySystem> systemClass, long updateTime, long laterExecutionTime) {
        this.systemClass = systemClass;
        this.updateTime = updateTime;
        this.laterExecutionTime = laterExecutionTime;
        return this;
    }

    @Override
    public void reset() {
        systemClass = null;
        updateTime = 0;
        laterExecutionTime = 0;
    }
}
