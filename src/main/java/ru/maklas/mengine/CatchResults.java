package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CatchResults<T> extends Array<T> {

    /** Returns first element if it exists. Otherwise returns null **/
    @Nullable
    public T first(){
        if (size == 0){
            return null;
        }
        return items[0];
    }

    /** Throws RuntimeException if there is no events caught **/
    public CatchResults<T> assertNotEmpty(){
        if (size == 0) throw new AssertionError("Catch result must not be empty");
        return this;
    }

    /** Calls Consumer on the first element of the Catch result if one exists **/
    public void ifFirstExists(Consumer<T> c){
        if (size > 0){
            c.accept(items[1]);
        }
    }

}
