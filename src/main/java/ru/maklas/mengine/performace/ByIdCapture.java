package ru.maklas.mengine.performace;

public class ByIdCapture extends Capture{

    long avgTimePerFrame;
    double callsPerFrame;

    public ByIdCapture(long averageNanoSeconds, long maxNanoSeconds, long minNanoSeconds, int framesChecked, long avgTimePerFrame, double callsPerFrame) {
        super(averageNanoSeconds, maxNanoSeconds, minNanoSeconds, framesChecked);
        this.avgTimePerFrame = avgTimePerFrame;
        this.callsPerFrame = callsPerFrame;
    }
}
