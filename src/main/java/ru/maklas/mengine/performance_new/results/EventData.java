package ru.maklas.mengine.performance_new.results;

public class EventData {

    public Class eventClass;
    public long totalTime;
    public int calls;
    public long internalTime;

    public EventData(Class eventClass) {
        this.eventClass = eventClass;
    }
}
