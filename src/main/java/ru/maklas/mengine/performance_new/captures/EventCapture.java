package ru.maklas.mengine.performance_new.captures;

import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.EntitySystem;

public class EventCapture implements Pool.Poolable{

    public Class eventClass;
    public long started;
    public long interrupted;
    public long resumed;
    public long finished;
    @Nullable public Class<? extends EntitySystem> entitySystem;


    public boolean wasInterrupted(){
        return interrupted != 0;
    }

    @Override
    public void reset() {
        eventClass = null;
        entitySystem = null;
        started = 0;
        interrupted = 0;
        resumed = 0;
        finished = 0;
    }

    public EventCapture from(EventCapture capture){
        this.eventClass = capture.eventClass;
        this.started = capture.started;
        this.interrupted = capture.interrupted;
        this.resumed = capture.resumed;
        this.finished = capture.finished;
        this.entitySystem = capture.entitySystem;
        return this;
    }
}
