package ru.maklas.mengine;

import com.badlogic.gdx.math.MathUtils;
import ru.maklas.mengine.components.IRenderComponent;

public class Test extends EntitySystem {

    public static final int updateFrames = 1000;

    public static void main(String[] args) {
        final TestEngine engine = new TestEngine();

        engine.add(new UpdatableEntitySystem());
        engine.add(new Test());
        engine.add(new RenderSystem());
        engine.subscribe(new Subscription<Object>(Object.class) {
            @Override
            public void receive(Object e) {
                engine.dispatch(new Entity());
            }
        });

        engine.addListener(new EntityListener() {
            @Override
            public void entityAdded(Entity e) {

            }

            @Override
            public void entityRemoved(Entity e) {

            }
        });
        engine.subscribe(new Subscription<Object>(Object.class) {
            @Override
            public void receive(Object event) {

            }
        });

        for (int i = 0; i < updateFrames; i++) {
            engine.update(0.016f);
            engine.render();
        }

        System.out.println(engine.captureResults().toString());

    }


    @Override
    public void update(float dt) {

        for (int i = 0; i < 100; i++) {
            Entity entity = new Entity(MathUtils.random(100000));
            getEngine().add(entity);
            entity.add(new Component() {});
            if (i == 99){
                getEngine().dispatchLater(new Object());
            }
        }

        if (getEngine().getEntities().size() > 1000){
            getEngine().executeAfterUpdate(new Runnable() {
                @Override
                public void run() {
                    getEngine().removeAllEntities();
                }
            });
        }


        Entity byId = getEngine().findById(4);
        for (int i = 0; i < MathUtils.random(4); i++) {
            getEngine().dispatchLater(new Object());
        }
        getEngine().invalidateRenderZ();
    }


    private static class RenderSystem extends IterableZSortedRenderSystem<IRenderComponent>{
        public RenderSystem() {
            super(IRenderComponent.class, false);
            setAlwaysInvalidate(true);
        }

        @Override
        protected void renderStarted() {
            getEngine().findById(1);
            getEngine().findById(1);
            getEngine().findById(1);
        }

        @Override
        protected void renderEntity(Entity entity, IRenderComponent rc) {
        }

        @Override
        protected void renderFinished() {

        }
    }
}
