package ru.maklas.mengine;

/**
 * Normalizes entity angle from 0 to 360
 */
public class AngleNormalizator360 implements AngleNormalizator {


    @Override
    public float normalize(float angle) {
        return (angle %= 360) < 0 ? angle + 360 : angle;
    }
}
