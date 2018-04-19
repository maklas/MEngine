package ru.maklas.mengine;

import ru.maklas.mengine.components.IRenderComponent;
import ru.maklas.mengine.utils.Signal;

public class Test extends EntitySystem{

    public static void main(String[] args) {
        PerformanceTestEngine engine = new PerformanceTestEngine();

        engine.add(new UpdatableEntitySystem());
        engine.add(new Test());
        engine.add(new RenderSystem());

        engine.addListener(new EntityListener() {
            @Override
            public void entityAdded(Entity e) {
                String s = "added";
            }

            @Override
            public void entityRemoved(Entity e) {
                String s = "removed";
            }
        });
        engine.subscribe(new Subscription<Object>(Object.class) {
            @Override
            public void receive(Signal<Object> signal, Object event) {
                String s = "Event!";
            }
        });

        for (int i = 0; i < 200; i++) {
            engine.update(0.016f);
            engine.render();
        }

        System.out.println(engine.captureResults());

    }


    @Override
    public void update(float dt) {

        for (int i = 0; i < 100; i++) {
            Entity entity = new Entity();
            getEngine().add(entity);
            entity.add(new Component() {
            });
        }


        Entity byId = getEngine().getById(4);
        for (int i = 0; i < 1000; i++) {
            getEngine().dispatchLater(new Object());
        }
    }


    private static class RenderSystem extends IterableZSortedRenderSystem<IRenderComponent>{
        public RenderSystem() {
            super(IRenderComponent.class);
        }

        @Override
        protected void renderStarted() {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void renderEntity(Entity entity, IRenderComponent rc) {
        }

        @Override
        protected void renderFinished() {
            invalidate();
        }
    }
}
