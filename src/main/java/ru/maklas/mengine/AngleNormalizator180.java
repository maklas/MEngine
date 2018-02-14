package ru.maklas.mengine;

public class AngleNormalizator180 implements AngleNormalizator {

    @Override
    public float normalize(float angle) {

        angle %= 360;

        angle = (angle + 360) % 360;

        if (angle > 180)
            angle -= 360;
        return angle > 180 ? angle - 360 : angle;
    }
}
