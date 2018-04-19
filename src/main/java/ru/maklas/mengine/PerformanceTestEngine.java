package ru.maklas.mengine;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.maklas.mengine.performace.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PerformanceTestEngine extends Engine {

    public static int frames = 180;

    Map<EntitySystem, LongAverager> systemTimerMap;
    Map<Class, LongAverager> eventTimerMap;
    Map<Class, DoubleAverager> eventAvgCallMap;
    Map<Class, Integer> thisFrameEventCallMap;
    LongAverager delayedOperationsTimer;
    LongAverager engineTimer;
    LongAverager entityAddTimer;
    LongAverager entityRemovalTimer;
    LongAverager renderTimer;

    LongAverager byIdTimer;
    LongAverager byIdTimerPerFrame;
    long byIdTotalTimeThisFrame = 0;
    DoubleAverager byIdCallCounter;
    int byIdCallsThisFrame = 0;

    public PerformanceTestEngine() {
        systemTimerMap = new HashMap<EntitySystem, LongAverager>();
        eventTimerMap = new HashMap<Class, LongAverager>();
        delayedOperationsTimer = new LongAverager(frames);
        engineTimer = new LongAverager(frames);
        entityAddTimer = new LongAverager(frames);
        entityRemovalTimer = new LongAverager(frames);
        renderTimer = new LongAverager(frames);
        byIdTimer = new LongAverager(frames);
        byIdTimerPerFrame = new LongAverager(frames);
        byIdCallCounter = new DoubleAverager(frames);
        eventAvgCallMap = new HashMap<Class, DoubleAverager>();
        thisFrameEventCallMap = new HashMap<Class, Integer>();
    }

    @Override
    public void add(EntitySystem system) {
        super.add(system);
        if (!(system instanceof RenderEntitySystem)) {
            systemTimerMap.put(system, new LongAverager(frames));
        }
    }

    @Nullable
    @Override
    public Entity getById(int id) {
        long start = System.nanoTime();
        Entity byId = super.getById(id);
        long end = System.nanoTime();
        long lasted = end - start;
        byIdTimer.addLong(lasted);
        byIdTotalTimeThisFrame += lasted;
        byIdCallsThisFrame++;
        return byId;
    }

    @Override
    public void update(float dt) {
        countCPFs();
        long beforeUpdate = System.nanoTime();
        if(this.updating) {
            throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
        }
        updating = true;

        Array<EntitySystem> systems = systemManager.getSystems();

        for (EntitySystem system : systems) {

            long beforeSystem = System.nanoTime();
            if (system.isEnabled()) {
                system.update(dt);
            }
            long afterSystem = System.nanoTime();
            systemTimerMap.get(system).addLong(afterSystem - beforeSystem);
        }

        updating = false;

        long beforePendings = System.nanoTime();
        processPendingOperations();
        long afterUpdate = System.nanoTime();
        delayedOperationsTimer.addLong(afterUpdate - beforePendings);
        engineTimer.addLong(afterUpdate - beforeUpdate);
    }

    private void countCPFs() {
        byIdCallCounter.addDouble(byIdCallsThisFrame);
        byIdTimerPerFrame.addLong(byIdTotalTimeThisFrame);
        for (Map.Entry<Class, Integer> e : thisFrameEventCallMap.entrySet()) {
            DoubleAverager avgr = eventAvgCallMap.get(e.getKey());
            if (avgr == null){
                avgr = new DoubleAverager(frames);
                eventAvgCallMap.put(e.getKey(), avgr);
            }
            avgr.addDouble(e.getValue());
        }

        thisFrameEventCallMap.clear();
        byIdTotalTimeThisFrame = 0;
        byIdCallsThisFrame = 0;
    }

    @Override
    public void render() {
        long beforeRedner = System.nanoTime();
        super.render();
        long afterRender = System.nanoTime();
        renderTimer.addLong(afterRender - beforeRedner);
    }

    @Override
    public Engine add(@NotNull Entity entity) {
        long before = System.nanoTime();
        Engine add = super.add(entity);
        long after = System.nanoTime();
        entityAddTimer.addLong(after - before);
        return add;
    }

    @Override
    public void dispatch(Object event) {
        Class<?> eventClass = event.getClass();
        LongAverager timer = eventTimerMap.get(eventClass);
        if (timer == null){
            timer = new LongAverager(frames);
            eventTimerMap.put(eventClass, timer);
        }
        long start = System.nanoTime();
        dispatcher.dispatch(event);
        long end = System.nanoTime();
        timer.addLong(end - start);
        Integer integer = thisFrameEventCallMap.get(eventClass);
        if (integer == null){
            integer = 0;
        }
        integer++;
        thisFrameEventCallMap.put(eventClass, integer);
    }

    @Override
    public void dispatchLater(final Object event) {
        pendingOperations.addLast(new Runnable() {
            @Override
            public void run() {
                dispatch(event);
            }
        });
    }

    @Override
    public boolean remove(@NotNull Entity entity) {
        long before = System.nanoTime();
        boolean removed = super.remove(entity);
        long after = System.nanoTime();
        entityRemovalTimer.addLong(after - before);
        return removed;
    }

    public PerformanceCapture captureResults() {
        Capture delayedOpCapture = fromTimer(delayedOperationsTimer);
        Capture engineCapture = fromTimer(engineTimer);
        ClassCapture renderCapture = fromTimer(renderTimer, systemManager.getRenderSystem() == null ? null : systemManager.getRenderSystem().getClass());
        Capture entityAddCapture = fromTimer(entityAddTimer);
        Capture entityRemoveCapture = fromTimer(entityRemovalTimer);
        ByIdCapture byIdCapture = fromTimer(byIdTimer, byIdTimerPerFrame.getAvg(), byIdCallCounter.getAvg());
        Array<ClassCapture> systemCaptures = new Array<ClassCapture>();

        Set<Map.Entry<EntitySystem, LongAverager>> entries = systemTimerMap.entrySet();
        for (Map.Entry<EntitySystem, LongAverager> entry : entries) {
            EntitySystem system = entry.getKey();
            LongAverager averager = entry.getValue();
            ClassCapture systemCapture = new ClassCapture(averager.getAvg(), averager.getMax(), averager.getMin(), averager.size(), system.getClass());
            systemCaptures.add(systemCapture);
        }

        Array<EventCapture> eventCapture = new Array<EventCapture>();
        Set<Map.Entry<Class, LongAverager>> eventEntries = eventTimerMap.entrySet();
        for (Map.Entry<Class, LongAverager> entry : eventEntries) {
            LongAverager averager = entry.getValue();
            EventCapture capt = new EventCapture(averager.getAvg(), averager.getMax(), averager.getMin(), averager.size(), entry.getKey(), eventAvgCallMap.get(entry.getKey()).getAvg());
            eventCapture.add(capt);
        }

        return new PerformanceCapture(
                systemCaptures,
                eventCapture,
                engineCapture,
                renderCapture,
                delayedOpCapture,
                entityAddCapture,
                entityRemoveCapture,
                byIdCapture);
    }

    private static Capture fromTimer(LongAverager timer){
        return new Capture(timer.getAvg(), timer.getMax(), timer.getMin(), timer.size());
    }

    private static ClassCapture fromTimer(LongAverager timer, Class clazz){
        return new ClassCapture(timer.getAvg(), timer.getMax(), timer.getMin(), timer.size(), clazz);
    }

    private static ByIdCapture fromTimer(LongAverager timer, long avgTimePerFrame, double callsPerFrame){
        return new ByIdCapture(timer.getAvg(), timer.getMax(), timer.getMin(), timer.size(), avgTimePerFrame, callsPerFrame);
    }
}
