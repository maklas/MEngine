package ru.maklas.mengine.utils;

import ru.maklas.mengine.AngleNormalizer;

/** Does not normalizes angle at all, so it might be any value **/
public class AngleNormalizerNONE implements AngleNormalizer {

    @Override
    public float normalize(float angle) {
        return angle;
    }
}
