package ru.maklas.mengine;

import org.junit.Test;

public class EntityTest {


    @Test
    public void testComponentCreation() throws Exception {

        Entity entity = new Entity();


        Component a = new Component1(15);
        Component b = new Component2(10, -90);

        entity
                .add(a)
                .add(b);

        System.out.println(entity.get(ComponentMapper.of(Component1.class)));
        System.out.println(entity.get(ComponentMapper.of(Component2.class)));
    }


    private class Component1 implements Component{

        float angle;

        public Component1(float angle) {
            this.angle = angle;
        }

        @Override
        public String toString() {
            return "Component1{" +
                    "angle=" + angle +
                    '}';
        }
    }


    private class Component2 implements Component{

        float xSpeed;
        float ySpeed;

        public Component2(float xSpeed, float ySpeed) {
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
        }

        @Override
        public String toString() {
            return "Component2{" +
                    "xSpeed=" + xSpeed +
                    ", ySpeed=" + ySpeed +
                    '}';
        }
    }
}