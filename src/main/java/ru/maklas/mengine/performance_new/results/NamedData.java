package ru.maklas.mengine.performance_new.results;

public class NamedData {

    public String name;
    public int calls;
    public long totalTime;
    public long min = 999999999999999L;
    public long max;

    public NamedData(String name) {
        this.name = name;
    }
}
