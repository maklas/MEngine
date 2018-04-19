package ru.maklas.mengine.performace;

import org.jetbrains.annotations.NotNull;

public class ClassCapture extends Capture implements Comparable<ClassCapture>{

    Class clazz;

    public ClassCapture(long averageNanoSeconds, long maxNanoSeconds, long minNanoSeconds, int framesChecked, Class clazz) {
        super(averageNanoSeconds, maxNanoSeconds, minNanoSeconds, framesChecked);
        this.clazz = clazz;
    }



    @Override
    public int compareTo(@NotNull ClassCapture o) {
        return (int)(o.averageNanoSeconds - averageNanoSeconds);
    }
}
