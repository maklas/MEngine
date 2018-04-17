package ru.maklas.mengine;

/**
 * Normalizes entity angle from 0 to 360
 */
public class AngleNormalizer360 implements AngleNormalizer {

    @Override
    public float normalize(float angle) {
        return (angle %= 360) < 0 ? angle + 360 : angle;
    }
}
