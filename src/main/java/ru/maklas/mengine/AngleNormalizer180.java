package ru.maklas.mengine;

/**
 * Normalizes entity angle from -180 to 180
 */
public class AngleNormalizer180 implements AngleNormalizer {

    @Override
    public float normalize(float angle) {

        angle %= 360;

        angle = (angle + 360) % 360;

        if (angle > 180)
            angle -= 360;
        return angle > 180 ? angle - 360 : angle;
    }
}
