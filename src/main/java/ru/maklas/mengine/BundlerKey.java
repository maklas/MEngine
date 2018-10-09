package ru.maklas.mengine;

import org.jetbrains.annotations.NotNull;

public class BundlerKey<T> {

    String key;
    Class<T> clazz;

    private BundlerKey(String key, Class<T> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    public static <T> BundlerKey<T> of(@NotNull String key, @NotNull Class<T> type){
        return new BundlerKey<T>(key, type);
    }

    @Override
    public String toString() {
        return "BundlerKey{" + "key='" + key + '\'' + ", clazz=" + clazz.getSimpleName() + '}';
    }
}
