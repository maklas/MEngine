package ru.maklas.mengine.performance_new.captures;

import ru.maklas.mengine.EntitySystem;

public class SystemCapture {

    public Class<? extends EntitySystem> systemClass;
    public long updateTime;
    public long laterExecutionTime;

    public SystemCapture(Class<? extends EntitySystem> systemClass, long updateTime, long laterExecutionTime) {

        this.systemClass = systemClass;
        this.updateTime = updateTime;
        this.laterExecutionTime = laterExecutionTime;
    }
}
