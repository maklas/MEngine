package ru.maklas.mengine;

import org.junit.Test;
import ru.maklas.mengine.components.RenderComponent;
import ru.maklas.mengine.systems.RenderSystem;

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


    @Test
    public void testGroups() throws Exception{

        Engine engine = new Engine();
        Entity entity = new Entity();
        entity.id = 5;
        engine.add(new RenderSystem());
        engine.add(entity);

        System.out.println("Frame: 0");
        engine.update(0.16f);

        System.out.println("Frame: 1");
        engine.update(0.16f);

        System.out.println("Frame: 2");
        engine.update(0.16f);

        System.out.println("Frame: 3");
        engine.update(0.16f);
        entity.add(new RenderComponent());

        System.out.println("Frame: 4");
        engine.update(0.16f);

        System.out.println("Frame: 5");
        engine.update(0.16f);

        System.out.println("Frame: 6");
        engine.update(0.16f);
        entity.remove(RenderComponent.class);

        System.out.println("Frame: 7");
        engine.update(0.16f);

        System.out.println("Frame: 8");
        engine.update(0.16f);




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