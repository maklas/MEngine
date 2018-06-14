package ru.maklas.mengine.performance_new.captures;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.EntitySystem;

public class FindByIDCapture {


    public int id;
    public long time;
    @Nullable public Class<? extends EntitySystem> systemClass;

    public FindByIDCapture(int id, long time, @Nullable Class<? extends EntitySystem> systemClass) {
        this.id = id;
        this.time = time;
        this.systemClass = systemClass;
    }
}
