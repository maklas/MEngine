package ru.maklas.mengine.performance_new.captures;

import com.badlogic.gdx.utils.Pool;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.EntitySystem;

public class FindByIDCapture implements Pool.Poolable{

    public int id;
    public long time;
    @Nullable public Class<? extends EntitySystem> systemClass;

    public FindByIDCapture() {

    }

    public FindByIDCapture(int id, long time, @Nullable Class<? extends EntitySystem> systemClass) {
        this.id = id;
        this.time = time;
        this.systemClass = systemClass;
    }

    public FindByIDCapture init(int id, long time, @Nullable Class<? extends EntitySystem> systemClass) {
        this.id = id;
        this.time = time;
        this.systemClass = systemClass;
        return this;
    }

    @Override
    public void reset() {
        id = 0;
        time = 0;
        systemClass = null;
    }
}
