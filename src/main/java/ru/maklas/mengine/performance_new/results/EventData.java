package ru.maklas.mengine.performance_new.results;

import java.util.Comparator;

public class EventData {

    public Class eventClass;
    public long totalTime;
    public int calls;
    public long selfTime;

    public EventData(Class eventClass) {
        this.eventClass = eventClass;
    }

    public static Comparator<EventData> totalTimeComparator = new Comparator<EventData>() {
        @Override
        public int compare(EventData o1, EventData o2) {
            return (int) (o2.totalTime - o1.totalTime);
        }
    };

    public static Comparator<EventData> internalTimeComparator = new Comparator<EventData>() {
        @Override
        public int compare(EventData o1, EventData o2) {
            return (int) (o2.selfTime - o1.selfTime);
        }
    };

    public static Comparator<EventData> callComparator = new Comparator<EventData>() {
        @Override
        public int compare(EventData o1, EventData o2) {
            return o2.calls - o1.calls;
        }
    };


    public static Comparator<EventData> getComparator(int type){
        switch (type){
            case PerformanceResult.CALLS:
                return callComparator;
            case PerformanceResult.INTERNAL:
                return internalTimeComparator;
            case PerformanceResult.TOTAL:
                return totalTimeComparator;
        }
        return null;
    }
}
