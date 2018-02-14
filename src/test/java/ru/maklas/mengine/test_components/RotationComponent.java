package ru.maklas.mengine.test_components;

import ru.maklas.mengine.Component;

public class RotationComponent implements Component{

    public float anglePerSecond;


    public RotationComponent(float anglePerSecond) {
        this.anglePerSecond = anglePerSecond;
    }

    public RotationComponent() {

    }



}
