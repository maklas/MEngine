package ru.maklas.mengine;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.components.IRenderComponent;
import ru.maklas.mengine.utils.SuperEventDispatcher;

public class Test extends EntitySystem {

    public static final int updateFrames = 1000;

    public static void main(String[] args) {
        SuperEventDispatcher superEventDispatcher = new SuperEventDispatcher();
        superEventDispatcher.registerClass(Engine.class);
        superEventDispatcher.registerClass(TestEngine.class);

        superEventDispatcher.subscribe(new Subscription<Engine>(Engine.class) {
            @Override
            public void receive(Engine e) {
                System.out.println("Engine listener triggered. Processing instance of " + e.getClass().getSimpleName());
            }
        });

        superEventDispatcher.subscribe(new Subscription<TestEngine>(TestEngine.class) {
            @Override
            public void receive(TestEngine e) {
                System.out.println("TestEngine listener triggered. Processing instance of " + e.getClass().getSimpleName());
            }
        });

        superEventDispatcher.dispatch(new TestEngine());



        if (true) return;
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

        BundlerKey<Array<String>> key = BundlerKey.of("sdf");

        Array<String> strings = engine.getBundler().get(key);
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
        getEngine().invalidateRender();
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
