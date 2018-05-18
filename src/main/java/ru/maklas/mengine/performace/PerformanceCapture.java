package ru.maklas.mengine.performace;

import com.badlogic.gdx.utils.Array;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class PerformanceCapture {

    NumberFormat percentFormat = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    NumberFormat microFormat = new DecimalFormat("#0.0", new DecimalFormatSymbols(Locale.ENGLISH));
    Array<ClassCapture> systemCaptureArray;
    Array<EventCapture> eventCaptureArray;
    Capture frameCapture;
    Capture engineCapture;
    ClassCapture renderCapture;
    Capture delayedOperationsCapture;
    Capture entityAddCapture;
    Capture entityRemovedCapture;
    ByIdCapture byIdCapture;

    public PerformanceCapture(Array<ClassCapture> systemCaptureArray,
                              Array<EventCapture> eventCaptureArray,
                              Capture updateCapture,
                              ClassCapture renderCapture,
                              Capture delayedOperationsCapture,
                              Capture entityAddCapture,
                              Capture entityRemovedCapture,
                              ByIdCapture byIdCapture) {
        this.systemCaptureArray = systemCaptureArray;
        this.eventCaptureArray = eventCaptureArray;
        this.engineCapture = updateCapture;
        this.renderCapture = renderCapture;
        this.delayedOperationsCapture = delayedOperationsCapture;
        this.entityAddCapture = entityAddCapture;
        this.entityRemovedCapture = entityRemovedCapture;
        this.byIdCapture = byIdCapture;
        if (renderCapture.clazz != null) systemCaptureArray.add(renderCapture);
        systemCaptureArray.sort();
        eventCaptureArray.sort();
        frameCapture = new Capture(
                updateCapture.averageNanoSeconds + renderCapture.averageNanoSeconds,
                updateCapture.maxNanoSeconds + renderCapture.maxNanoSeconds,
                updateCapture.minNanoSeconds + renderCapture.minNanoSeconds, updateCapture.framesChecked);
    }

    public Array<ClassCapture> getSystemCaptureArray() {
        return systemCaptureArray;
    }

    public Capture getEngineCapture() {
        return engineCapture;
    }

    public Capture getDelayedOperationsCapture() {
        return delayedOperationsCapture;
    }


    /**
     * <li>
     *     <b>Engine total</b> - update(dt) + render() methods combined. Shows how much time did Engine took per frame
     * </li>
     * <li>
     *     <b>Engine update</b> - how much time did it take for all systems to be updated and delayed operations to be processed.
     * </li>
     * <li>
     *     <b>Engine render</b> - how much time did it take for engine.render()
     * </li>
     * <li>
     *     <b>Engine findById</b> - shows how much time did it take to find Entity by it's id using engine.findById().
     *     <b>avg</b>, <b>min</b> and <b>max</b> indicates time per call, not per frame. However <b>framePart</b> measures
     *     how much time in percents this method took.
     *     CPF means Calls Per Frame which indicates how many times findById() is called on each frame.
     * </li>
     * <li>
     *     <b>Delayed operations</b> - how much time did it take for delayed operations to be processed.
     *     This includes: engine.dispatchLater(), engine.add(entity) during engine update and engine.executeAfterUpdate(runnable).
     * </li>
     * <li>
     *     <b>Entity add</b> - shows how much time did it take for Entities to be added to engine.
     * </li>
     * <li>
     *     <b>Entity remove</b> - shows how much time did it take for Entities to be removed from engine.
     * </li>
     * <li>
     *     <b>Systems</b> - shows frame time for each individual System you have in engine by their class name.
     * </li>
     * <li>
     *     <b>Events</b> - shows frame time for fired and handled events by event class name. CPS stands for Calls Per Second
     * </li>
     */
    @Override
    public String toString() {
        if (systemCaptureArray.size == 0){
            return "NO SYSTEMS";
        }

        int systemClassNameMinimalSize = systemCaptureArray.get(0).clazz.getSimpleName().length();
        for (ClassCapture systemCapture : systemCaptureArray) {
            String className = systemCapture.clazz.getSimpleName();
            if (className.length() > systemClassNameMinimalSize){
                systemClassNameMinimalSize = className.length();
            }
        }

        int eventClassNameMinimalSize = eventCaptureArray.size == 0 ? 1 : eventCaptureArray.get(0).clazz.getSimpleName().length();
        for (ClassCapture systemCapture : eventCaptureArray) {
            String className = systemCapture.clazz.getSimpleName();
            if (className.length() > eventClassNameMinimalSize){
                eventClassNameMinimalSize = className.length();
            }
        }

        StringBuilder systemBuilder = new StringBuilder();
        for (ClassCapture capture : systemCaptureArray) {
            systemBuilder.append(captureRes(capture, systemClassNameMinimalSize)).append('\n');
        }

        StringBuilder eventBuilder = new StringBuilder();
        for (EventCapture capture : eventCaptureArray) {
            eventBuilder.append(captureResEvent(capture, eventClassNameMinimalSize)).append('\n');
        }

        return "Using last " + engineCapture.framesChecked + " frames" + '\n' +
                "---------------CAPTURE---------------" + '\n' +
                "Engine total       -> " + captureRes(frameCapture) + '\n' +
                "Engine update      -> " + captureRes(engineCapture) + '\n' +
                "Engine render      -> " + captureRes(renderCapture) + '\n' +
                "Engine findById     -> " + captureRes(byIdCapture) + '\n' +
                "Delayed operations -> " + captureRes(delayedOperationsCapture) + '\n' +
                "Entity add         -> " + captureRes(entityAddCapture) + '\n' +
                "Entity remove      -> " + captureRes(entityRemovedCapture) + '\n' +
                "---------------SYSTEMS---------------" + '\n' +
                systemBuilder.toString() +
                "---------------EVENTS---------------" + '\n' +
                eventBuilder.toString() +
                "---------------CAPTURE--------------";
    }

    private String captureRes(ClassCapture capture, int classnameSize){
        return addSpacesRight(capture.clazz.getSimpleName(), classnameSize) + " -> " + captureRes(capture);
    }

    private String captureResEvent(EventCapture capture, int classnameSize){
        return addSpacesRight(capture.clazz.getSimpleName(), classnameSize) + " -> " + captureResEvent(capture);
    }

    private String captureRes(Capture capture){
        return addSpacesRight("avg: " + micro(capture.averageNanoSeconds) + ", ", 17) + addSpacesRight("max: " + micro(capture.maxNanoSeconds) + ", ", 17) + addSpacesRight("min: " + micro(capture.minNanoSeconds) + ", ", 17) + ("framePart: " + framePercent(capture.averageNanoSeconds));
    }

    private String captureResEvent(EventCapture capture){
        return addSpacesRight("avg: " + micro(capture.averageNanoSeconds) + ", ", 17) + addSpacesRight("max: " + micro(capture.maxNanoSeconds) + ", ", 17) + addSpacesRight("min: " + micro(capture.minNanoSeconds) + ", ", 17) + ("framePart: " + framePercent(capture.averageNanoSeconds)) + ", CPS: " + microFormat.format(capture.callsPerFrame * 60d);
    }

    private String captureRes(ByIdCapture capture){
        return addSpacesRight("avg: " + micro(capture.averageNanoSeconds) + ", ", 17) + addSpacesRight("max: " + micro(capture.maxNanoSeconds) + ", ", 17) + addSpacesRight("min: " + micro(capture.minNanoSeconds) + ", ", 17) + ("framePart: " + framePercent((capture.avgTimePerFrame)) + ", CPF: " + microFormat.format(capture.callsPerFrame));
    }

    private String micro(long nano){
        double microseconds = ((double) nano) / 1000;
        return microFormat.format(microseconds) + " us";
    }

    private String framePercent(long nano){
        double frameTimeNano = 1.6666666666666666E7;
        double percent = (nano / frameTimeNano) * 100;
        return percentFormat.format(percent) + '%';
    }

    private static String addSpacesRight(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    private static String addSpacesLeft(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        builder.append(s);
        return builder.toString();
    }
}
