package ru.maklas.mengine.performance_new.results;

import ru.maklas.mengine.EntitySystem;

public class SystemData {

    public Class<? extends EntitySystem> clazz;
    public long totalTime;
    public long totalLaterTime;
    public long minTime = 9999999999999999L;
    public long maxTime;
    public int updates;

    public SystemData(Class<? extends EntitySystem> clazz) {
        this.clazz = clazz;
    }
}
