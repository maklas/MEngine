package ru.maklas.mengine.performance_new.captures;

import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.EntitySystem;

public class EventCapture {

    public Class eventClass;
    public long started;
    public long interrupted;
    public long resumed;
    public long finished;
    @Nullable public Class<? extends EntitySystem> entitySystem;


    public boolean wasInterrupted(){
        return interrupted != 0;
    }

}
