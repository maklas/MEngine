package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.junit.Test;
import ru.maklas.mengine.test_components.MovementComponent;
import ru.maklas.mengine.test_components.RenderComponent;
import ru.maklas.mengine.test_components.RotationComponent;
import ru.maklas.mengine.test_systems.MovementSystem;
import ru.maklas.mengine.test_systems.RenderingSystem;
import ru.maklas.mengine.test_systems.RotationSystem;

import static org.junit.Assert.assertEquals;

public class EntityTest {


    @Test
    public void testAngleNormalization() throws Exception {


        Entity.angleNormalizator = new AngleNormalizator360();
        Entity entity = new Entity();

        int[] anglesToTest = new int[]{0, 100, 200, 300, 400, 500, 600, 36000001, -10000, -1000, -100};

        for (int i : anglesToTest) {
            entity.setAngle(i);
            float rightAngle = calculateAngleRight360(i);
            assertEquals("360 For " + i, rightAngle, entity.getAngle(), 0.1f);
        }

        Entity.angleNormalizator = new AngleNormalizator180();

        for (int i : anglesToTest) {
            entity.setAngle(i);
            float rightAngle = calculateAngleRight180(i);
            assertEquals("180 For " + i, rightAngle, entity.getAngle(), 0.1f);
        }
    }


    private float calculateAngleRight360(float angle){
        while (angle >= 360){
            angle -= 360;
        }

        while (angle < 0){
            angle += 360;
        }

        return angle;
    }

    private float calculateAngleRight180(float angle){
        while (angle >= 180){
            angle -= 360;
        }

        while (angle < -180){
            angle += 360;
        }

        return angle;
    }


    @Test
    public void testGroups() throws Exception{
        final Engine engine = new Engine();
        final Entity entity = new Entity();
        entity.add(new RotationComponent(1));
        engine.add(entity);

        EntitySystem entitySystem = new EntitySystem(){

            Group rotationGroup;
            ComponentMapper<RotationComponent> mapper;

            @Override
            public void onAddedToEngine(Engine engine) {
                super.onAddedToEngine(engine);
                rotationGroup = engine.getGroupManager().of(RotationComponent.class);
                mapper = ComponentMapper.of(RotationComponent.class);
            }


            @Override
            public void update(float dt) {
                Array<Entity> entityArray = rotationGroup.entityArray;

                for (Entity e : entityArray) {
                    RotationComponent rotationComponent = e.get(mapper);
                    e.angle += rotationComponent.anglePerSecond * dt;
                }
            }
        };

        engine.add(entitySystem);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 60 * 6; i++) {
                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    engine.update(1 / 60f);
                    System.out.println(entity.angle);
                }

                assertEquals(6, entity.angle, 0.1f);
            }
        });
        thread.start();


        thread.join();

    }


    @Test
    public void testSystems() throws Exception {
        Engine engine = new Engine();
        engine.add(new MovementSystem());
        engine.add(new RotationSystem());
        engine.add(new RenderingSystem());

        Entity a = new Entity(5);
        a.x = 10;
        a.y = 10;
        a.setAngle(100);
        a.zOrder = 90;
        a.add(new MovementComponent(10, 10));
        a.add(new RotationComponent(10));
        a.add(new RenderComponent());


        Entity b = new Entity(1);
        b.x = -4;
        b.y = 149;
        b.setAngle(0);
        b.zOrder = 10;
        b.add(new MovementComponent(10, 10));
        b.add(new RotationComponent(10));
        b.add(new RenderComponent());

        engine.add(a).add(b);

        new UpdateThread(engine, 5).run();

        assertEquals(60, a.x, 0.1f);
        assertEquals(60, a.y, 0.1f);
        assertEquals(150, a.getAngle(), 0.1f);

        assertEquals(46, b.x, 0.1f);
        assertEquals(199, b.y, 0.1f);
        assertEquals(50, b.getAngle(), 0.1f);
    }














    private class UpdateThread extends Thread{

        private final Engine engine;
        private final int seconds;

        public UpdateThread(Engine engine, int seconds) {
            this.engine = engine;
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < seconds * 60; i++) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                engine.update(1/60f);
                engine.render();
            }
        }
    }
}