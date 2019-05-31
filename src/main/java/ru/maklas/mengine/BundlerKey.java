package ru.maklas.mengine;

import org.jetbrains.annotations.NotNull;

/** A key that stores Type data of User-Object **/
public class BundlerKey<T> {

    String key;

    private BundlerKey(String key) {
        this.key = key;
    }

    public static <T> BundlerKey<T> of(@NotNull String key){
        return new BundlerKey<T>(key);
    }

    @Override
    public String toString() {
        return "BundlerKey{" + "key='" + key + '\'' + ", clazz=" + '}';
    }
}
