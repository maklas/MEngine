package ru.maklas.mengine.performace;

public class EventCapture extends ClassCapture{

    double callsPerFrame;

    public EventCapture(long averageNanoSeconds, long maxNanoSeconds, long minNanoSeconds, int framesChecked, Class clazz, double callsPerFrame) {
        super(averageNanoSeconds, maxNanoSeconds, minNanoSeconds, framesChecked, clazz);
        this.callsPerFrame = callsPerFrame;
    }

}
