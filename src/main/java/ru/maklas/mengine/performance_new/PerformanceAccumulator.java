package ru.maklas.mengine.performance_new;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.RenderEntitySystem;
import ru.maklas.mengine.performance_new.captures.EventCapture;
import ru.maklas.mengine.performance_new.results.PerformanceResult;

public class PerformanceAccumulator {

    public static int framesToCount = 180;
    private final FrameData[] frameDatas = new FrameData[framesToCount];
    private final int size = frameDatas.length;
    private int currentFrameCounter = 0;
    private FrameData currentFrame;
    private EventAccumulator eventAccumulator = new EventAccumulator();
    private Pool<EventCapture> eventCapturePool;


    //Temp Fields:
    private long updateStartTime; //start of Engine update
    private EntitySystem currentSystem; //Current EntitySystem that is getting updated
    private long systemStartTime;
    private long systemFinishTime;
    private long renderStarted;
    private long afterUpdateStart; //Start of processAfterUpdateOperations()

    public PerformanceAccumulator() {
        for (int i = 0; i < size; i++) {
            frameDatas[i] = new FrameData();
        }
        currentFrame = frameDatas[0];

        eventCapturePool = new Pool<EventCapture>() {
            @Override
            protected EventCapture newObject() {
                return new EventCapture();
            }
        };
    }



    //Engine.update();

    public void updateStarted(){
        int currentFrameId = currentFrameCounter++;
        currentFrame = frameDatas[currentFrameId];
        if (currentFrameCounter >= size){
            currentFrameCounter = 0;
        }
        currentFrame.reset();

        updateStartTime = System.nanoTime();
    }

    public void updateFinished(){
        long now = System.nanoTime();
        currentFrame.engineUpdateTime = now - updateStartTime;
    }




    //EntitySystem.update();
    //+ executeLater()

    public void systemStarted(EntitySystem system){
        systemStartTime = System.nanoTime();
        currentSystem = system;
    }

    public void systemFinished(){
        systemFinishTime = System.nanoTime();
    }

    public void laterExecutionFinished(boolean runnablesExecuted){
        if (runnablesExecuted){
            long now = System.nanoTime();
            currentFrame.addSystemUpdate(currentSystem.getClass(), systemFinishTime - systemStartTime, now - systemFinishTime);
        } else {
            currentFrame.addSystemUpdate(currentSystem.getClass(), systemFinishTime - systemStartTime);
        }
        currentSystem = null;
    }


    //RenderSystem.render();

    public void systemRenderStarted(RenderEntitySystem system){
        currentSystem = system;
        renderStarted = System.nanoTime();
    }

    public void systemRenderFinished(){
        long systemRenderTime = System.nanoTime() - renderStarted;
        currentFrame.engineRenderTime += systemRenderTime;
        currentFrame.addSystemUpdate(currentSystem.getClass(), systemRenderTime);
        currentSystem = null;
    }



    //processAfterUpdateOperations()
    public void afterUpdateStarted(){
        afterUpdateStart = System.nanoTime();
    }

    public void afterUpdateFinished(){
        currentFrame.afterUpdateTime = System.nanoTime() - afterUpdateStart;
    }



    //EventDispatch

    private Array<EventCapture> eventStack = new Array<EventCapture>();
    public void eventDispatchStarted(Object event){
        long now = System.nanoTime();
        if (eventStack.size != 0){
            EventCapture peek = eventStack.peek();
            if (peek.wasInterrupted()){
                peek.interrupted = now - (peek.resumed - peek.interrupted);
            } else {
                peek.interrupted = now;
            }
        }
        EventCapture capture = eventCapturePool.obtain();
        capture.eventClass = event.getClass();
        capture.entitySystem = currentSystem == null ? null : currentSystem.getClass();
        capture.started = now;
        eventStack.add(capture);
    }

    public void eventDispatchFinished(){
        EventCapture last = eventStack.pop();
        long now = System.nanoTime();
        last.finished = now;
        if (eventStack.size > 0){
            eventStack.peek().resumed = now;
        }
        currentFrame.eventDispatch(last);

        long totalTime = now - last.started;
        eventAccumulator.saveEvent(last.eventClass, totalTime, last.wasInterrupted() ? totalTime - (last.resumed - last.interrupted) : totalTime);
        eventCapturePool.free(last);
    }



    //FindById

    long findByIdStart;
    int findById;
    public void findByIdStarted(int id){
        findByIdStart = System.nanoTime();
        findById = id;
    }

    public void findByIdFinished(){
        currentFrame.findById(findById, System.nanoTime() - findByIdStart, currentSystem);
    }


    //Entity: add | remove

    Array<Long> entityAddCaptures = new Array<Long>();
    public void startedAddingEntity(){
        entityAddCaptures.add(System.nanoTime());
    }

    public void finishedAddingEntity(){
        long last = entityAddCaptures.pop();
        currentFrame.entityAdd(System.nanoTime() - last);
    }

    Array<Long> entityRemoveCaptures = new Array<Long>();
    public void startedRemovingEntity(){
        entityRemoveCaptures.add(System.nanoTime());
    }

    public void finishedRemovingEntity(){
        long last = entityRemoveCaptures.pop();
        currentFrame.entityRemove(System.nanoTime() - last);
    }

    public PerformanceResult captureResults(int entities) {
        return new PerformanceResult(frameDatas, eventAccumulator, entities);
    }
}
