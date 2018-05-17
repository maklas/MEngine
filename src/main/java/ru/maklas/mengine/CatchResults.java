package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;

public class CatchResults<T> extends Array<T> {

    @Nullable
    public T first(){
        if (size == 0){
            return null;
        }
        return items[0];
    }

    public CatchResults<T> assertNotEmpty(){
        if (size == 0) throw new AssertionError("Catch result must not be empty");
        return this;
    }

}
