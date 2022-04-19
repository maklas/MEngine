package ru.maklas.mengine;

import com.badlogic.gdx.utils.Predicate;
import ru.maklas.mengine.utils.Listener;

import java.util.function.Consumer;

/** Handy utils for Engine **/
public class EngineUtils {

    public static ComponentChecker makeChecker(Class<? extends Component>... components){
        int[] ids = new int[components.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = ComponentMapper.of(components[i]).id;
        }
        return new ComponentChecker(ids);
    }

    public static ComponentChecker makeChecker(ComponentMapper... mappers){
        int[] ids = new int[mappers.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = mappers[i].id;
        }
        return new ComponentChecker(ids);
    }

    public static void forEach(Iterable<Entity> entities, ComponentChecker checker, Consumer<Entity> consumer){
        for (Entity entity : entities) {
            if (checker.evaluate(entity)){
                consumer.accept(entity);
            }
        }
    }

    /**
     * Install an IdChecker that will check every time you add Entity to engine whether it's id is set.
     * If some Entities can have id of 0, just allow them in predicate
     */
    public static void installIdChecker(Engine engine, final Predicate<Entity> allowIdZeroPredicate){
       engine.addListener(new EntityListener() {
           @Override
           public void entityAdded(Entity e) {
               if (e.id == 0 && !allowIdZeroPredicate.evaluate(e)){
                   new Exception().printStackTrace();
               }
           }

           @Override
           public void entityRemoved(Entity e) {}
       });
    }

    /** Makes a subscription that will be called only once **/
    public static <T> Subscription<T> singleCallSubscription(Class<T> eventClass, final Listener<T> listener){
        return new Subscription<T>(eventClass) {
            private boolean called = false;
            @Override
            public void receive(T e) {
                if (!called){
                    listener.receive(e);
                    called = true;
                }
            }
        };
    }

    /** Makes Subscription that will only be enabled if system is enabled **/
    public static <T> Subscription<T> systemEnabledSubscription(final EntitySystem system, Class<T> eventClass, final Listener<T> listener){
        return new Subscription<T>(eventClass) {
            @Override
            public void receive(T e) {
                if (system.isEnabled()){
                    listener.receive(e);
                }
            }
        };
    }

}
