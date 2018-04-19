package ru.maklas.mengine.performace;

public class Capture {

    long averageNanoSeconds;
    long maxNanoSeconds;
    long minNanoSeconds;
    int framesChecked;

    public Capture(long averageNanoSeconds, long maxNanoSeconds, long minNanoSeconds, int framesChecked) {
        this.averageNanoSeconds = averageNanoSeconds;
        this.maxNanoSeconds = maxNanoSeconds;
        this.minNanoSeconds = minNanoSeconds;
        this.framesChecked = framesChecked;
    }
}
