package ru.maklas.mengine.performance_new.captures;

import com.badlogic.gdx.utils.Pool;

public class EntityCapture implements Pool.Poolable{

    public boolean add;
    public long time;

    public EntityCapture() {

    }

    public EntityCapture(boolean add, long time) {
        this.add = add;
        this.time = time;
    }

    public EntityCapture init(boolean add, long time) {
        this.add = add;
        this.time = time;
        return this;
    }


    public void reset(){
        this.time = 0;
    }


}
