package ru.maklas.mengine.test_components;

import ru.maklas.mengine.Component;

public class MovementComponent implements Component{

    public float x;
    public float y;

    public MovementComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MovementComponent() {
    }
}
