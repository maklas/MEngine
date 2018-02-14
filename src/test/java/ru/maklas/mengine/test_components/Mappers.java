package ru.maklas.mengine.test_components;

import ru.maklas.mengine.ComponentMapper;

public class Mappers {

    public static final ComponentMapper<MovementComponent> movementM = ComponentMapper.of(MovementComponent.class);
    public static final ComponentMapper<RotationComponent> rotationM = ComponentMapper.of(RotationComponent.class);
    public static final ComponentMapper<RenderComponent>   renderM   = ComponentMapper.of(RenderComponent.class);


}
