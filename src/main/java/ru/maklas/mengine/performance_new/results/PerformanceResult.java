package ru.maklas.mengine.performance_new.results;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.performance_new.EventAccumulator;
import ru.maklas.mengine.performance_new.FrameData;
import ru.maklas.mengine.performance_new.captures.EntityCapture;
import ru.maklas.mengine.performance_new.captures.EventCapture;
import ru.maklas.mengine.performance_new.captures.FindByIDCapture;
import ru.maklas.mengine.performance_new.captures.SystemCapture;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;

public class PerformanceResult {

    public static final int CALLS = 0;
    public static final int INTERNAL = 1;
    public static final int TOTAL = 2;

    public static int EVENT_SORT_TYPE = TOTAL;

    public int totalFrames;
    public int entities;
    public EventAccumulator eventAccumulator;
    public NamedData engineUpdate;
    public NamedData engineRender;
    public NamedData engineTotal;
    public NamedData engineAfterUpdate;
    public NamedData entityAdd;
    public NamedData entityRemove;
    public FindByIdData findById;
    public Array<SystemData> systemDatas;
    public Array<EventData> events;

    public PerformanceResult(FrameData[] frames, EventAccumulator eventAccumulator, int entities) {
        totalFrames = frames.length;
        this.entities = entities;
        systemDatas = extractSystemDatas(frames);
        this.eventAccumulator = eventAccumulator;
        engineTotal = new NamedData("Engine total");
        engineUpdate = new NamedData("Engine update");
        engineRender = new NamedData("Engine render");
        engineAfterUpdate = new NamedData("Engine afterUpdate");
        entityAdd = new NamedData("Entity add");
        entityRemove = new NamedData("Entity remove");

        extractTotal(engineTotal, frames);
        extractUpdate(engineUpdate, frames);
        extractRender(engineRender, frames);
        extractAfterUpdate(engineAfterUpdate, frames);
        extractAddRemove(entityAdd,  entityRemove, frames);

        findById = extractById(frames);
        events = extractEvents(frames);
        sortAll();
    }

    private void extractTotal(NamedData data, FrameData[] frames){
        for (FrameData frame : frames) {
            long frameTotal = frame.engineUpdateTime + frame.engineRenderTime;
            data.totalTime += frameTotal;
            data.updateMinMax(frameTotal);
            if (frameTotal > 0)
                data.calls++;
        }
    }

    private void extractUpdate(NamedData data, FrameData[] frames){
        for (FrameData frame : frames) {
            long frameTotal = frame.engineUpdateTime;
            data.totalTime += frameTotal;
            data.updateMinMax(frameTotal);
            if (frameTotal > 0)
                data.calls++;
        }
    }

    private void extractRender(NamedData data, FrameData[] frames){
        for (FrameData frame : frames) {
            long frameTotal = frame.engineRenderTime;
            data.totalTime += frameTotal;
            data.updateMinMax(frameTotal);
            if (frameTotal > 0)
                data.calls++;
        }
    }

    private void extractAfterUpdate(NamedData data, FrameData[] frames){
        for (FrameData frame : frames) {
            long frameTotal = frame.afterUpdateTime;
            data.totalTime += frameTotal;
            updateMinMax(data, frameTotal);
            if (frameTotal > 0)
                data.calls++;
        }
    }

    private void extractAddRemove(NamedData add, NamedData remove, FrameData[] frames){
        for (FrameData frame : frames) {
            for (EntityCapture entity : frame.entities) {
                NamedData data = entity.add ? add : remove;
                long total = entity.time;
                data.totalTime += total;
                data.updateMinMax(total);
                data.calls++;
            }
        }
    }

    private void updateMinMax(NamedData data, long val){
        if (data.max < val){
            data.max = val;
        }
        if (data.min > val){
            data.min = val;
        }
    }

    private SystemData getSystemData(Class<? extends EntitySystem> clazz){
        for (SystemData systemData : systemDatas) {
            if (systemData.clazz == clazz){
                return systemData;
            }
        }
        return null;
    }

    private Array<SystemData> extractSystemDatas(FrameData[] frames) {
        ObjectMap<Class<? extends EntitySystem>, SystemData> systemMap = new ObjectMap<Class<? extends EntitySystem>, SystemData>();
        for (FrameData frame : frames) {
            for (SystemCapture system : frame.systems) {
                Class<? extends EntitySystem> clazz = system.systemClass;

                SystemData systemData = systemMap.get(clazz);
                if (systemData == null){
                    systemData = new SystemData(clazz);
                    systemMap.put(clazz, systemData);
                }
                systemData.updates++;
                systemData.totalTime += system.updateTime;
                systemData.totalLaterTime += system.laterExecutionTime;
                if (systemData.maxTime < system.updateTime){
                    systemData.maxTime = system.updateTime;
                }
                if (systemData.minTime > system.updateTime){
                    systemData.minTime = system.updateTime;
                }
            }
        }


        return systemMap.values().toArray();
    }

    private FindByIdData extractById(FrameData[] frames) {
        FindByIdData ret = new FindByIdData();

        for (FrameData frame : frames) {
            for (FindByIDCapture find : frame.finds) {
                ret.calls++;
                ret.total += find.time;
                if (find.systemClass != null){
                    SystemData systemData = getSystemData(find.systemClass);
                    if (systemData != null) {
                        ret.getForSystem(systemData).calls++;
                    }
                }
            }
        }

        ret.frames = totalFrames;
        return ret;
    }

    private Array<EventData> extractEvents(FrameData[] frames) {
        ObjectMap<Class, EventData> map = new ObjectMap<Class, EventData>();

        for (FrameData frame : frames) {
            for (EventCapture event : frame.events) {
                EventData eventData = map.get(event.eventClass);
                if (eventData == null){
                    eventData = new EventData(event.eventClass);
                    map.put(event.eventClass, eventData);
                }

                long totalTime = event.finished - event.started;
                long internalTime;
                if (event.wasInterrupted()){
                    internalTime = totalTime - (event.resumed - event.interrupted);
                } else {
                    internalTime = totalTime;
                }

                eventData.totalTime += totalTime;
                eventData.selfTime += internalTime;
                eventData.calls++;
            }
        }

        return map.values().toArray();
    }

    private void sortAll(){
        systemDatas.sort(new Comparator<SystemData>() {
            @Override
            public int compare(SystemData o1, SystemData o2) {
                return (int) (o2.totalTime - o1.totalTime);
            }
        });

        events.sort(EventData.getComparator(EVENT_SORT_TYPE));
    }

    @Override
    public String toString() {
        return "Using last " + totalFrames + " frames" + '\n' +
                "---------------CAPTURE---------------" + '\n' +
                firstRowsToString() +
                "---------------SYSTEMS---------------" + '\n' +
                systemsToString() +
                "---------------EVENTS---------------" + '\n' +
                eventsToString() +
                "--------------ALL-EVENTS--------------" + '\n' +
                allTimeEventsToString() +
                (findById.calls > 0 ?
                "----------------BY ID----------------" + '\n' +
                byIdToString() : "") +
                "---------------CAPTURE--------------";
    }

    private String byIdToString() {
        StringBuilder builder = new StringBuilder();
        float cpf = (float) findById.calls / totalFrames;
        long avgNano = findById.total / findById.calls;

        builder.append("Calls: ").append(addSpacesRight(findById.calls + ", ", 10));
        builder.append("CPF: ").append(addSpacesRight(floatFormatted(cpf, 1) + ", ", 12));
        builder.append("avg: ").append(addSpacesRight(micro(avgNano) + ", ", 12));
        builder.append("perFrame: ").append(framePercent(findById.total / totalFrames));
        builder.append('\n');

        Array<FindByIdData.SystemCall> topUseSystems = findById.getTopUseSystems();

        int maxLen = 0;
        for (FindByIdData.SystemCall topUseSystem : topUseSystems) {
            int length = topUseSystem.data.clazz.getSimpleName().length();
            if (length > maxLen){
                maxLen = length;
            }
        }


        for (FindByIdData.SystemCall topUseSystem : topUseSystems) {
            if (topUseSystem.calls > 1){
                builder.append("     | ")
                        .append(addSpacesRight(topUseSystem.data.clazz.getSimpleName(), maxLen))
                        .append(" -> ").append(addSpacesRight(String.valueOf(topUseSystem.calls) + ", ", 9))
                        .append("perFrame: ").append(framePercent((long) (((float) topUseSystem.calls / totalFrames) * avgNano)))
                        .append('\n');
            }
        }


        return builder.toString();
    }

    private String systemsToString() {
        int maxLen = 0;
        for (SystemData systemData : systemDatas) {
            int classNameLen = systemData.clazz.getSimpleName().length();
            if (classNameLen > maxLen){
                maxLen = classNameLen;
            }
        }

        StringBuilder builder = new StringBuilder();

        for (SystemData systemData : systemDatas) {
            builder.append(addSpacesRight(systemData.clazz.getSimpleName(), maxLen));
            builder.append(" -> ");
            builder.append("avg: ").append(addSpacesRight(micro(systemData.totalTime / systemData.updates), 9)).append("(").append(framePercent(systemData.totalTime / systemData.updates)).append("),  ");
            builder.append("min: ").append(addSpacesRight(micro(systemData.minTime) + ", ", 10));
            builder.append("max: ").append(addSpacesRight(micro(systemData.maxTime) + ", ", 10));
            builder.append("later: ").append(micro(systemData.totalLaterTime));
            builder.append('\n');
        }


        return builder.toString();
    }

    private String eventsToString() {
        return eventsToString(events);
    }

    private String eventsToString(Array<EventData> events){
        int len = 0;
        for (EventData event : events) {
            int classNameLen = event.eventClass.getSimpleName().length();
            if (len < classNameLen){
                len = classNameLen;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (EventData event : events) {
            long selfTimePerCall = event.selfTime / event.calls;
            long totalTimePerCall = event.totalTime / event.calls;

            builder.append(addSpacesRight(event.eventClass.getSimpleName(), len));
            builder.append(" -> ");
            builder.append("calls: ").append(addSpacesRight(event.calls + ", ", 7));
            builder.append("self: ").append(addSpacesRight(micro(selfTimePerCall) + ", ", 10));
            builder.append("total: ").append(addSpacesRight(micro(totalTimePerCall) + ", ", 10));
            builder.append("perCall: ")
                    .append(framePercent(selfTimePerCall))
                    .append(" | ")
                    .append(framePercent(totalTimePerCall))
                    .append(",  ");
            builder.append("perFrame: ")
                    .append(framePercent(event.selfTime / totalFrames))
                    .append(" | ")
                    .append(framePercent(event.totalTime / totalFrames));
            builder.append('\n');
        }


        return builder.toString();
    }

    private String allTimeEventsToString() {
        Array<EventData> events = eventAccumulator.get();

        events.sort(EventData.getComparator(EVENT_SORT_TYPE));

        int len = 0;
        for (EventData event : events) {
            int classNameLen = event.eventClass.getSimpleName().length();
            if (len < classNameLen){
                len = classNameLen;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (EventData event : events) {
            builder.append(addSpacesRight(event.eventClass.getSimpleName(), len));
            builder.append(" -> ");
            builder.append("calls: ").append(addSpacesRight(event.calls + ", ", 8));
            builder.append("self: ").append(addSpacesRight(micro(event.selfTime / event.calls), 9)).append("(").append(framePercent(event.selfTime / event.calls)).append("),  ");
            builder.append("total: ").append(addSpacesRight(micro(event.totalTime / event.calls), 9)).append("(").append(framePercent(event.totalTime / event.calls)).append(")");
            builder.append('\n');
        }


        return builder.toString();
    }

    private String firstRowsToString(){
        int len = maxLength(
                engineTotal,
                engineUpdate,
                engineRender,
                engineAfterUpdate,
                entityAdd,
                entityRemove);

        return
                addSpacesRight("Engine entities", len) + " -> " + entities + '\n' +
                named(engineTotal, len) + '\n' +
                named(engineUpdate, len) + '\n' +
                named(engineRender, len) + '\n' +
                named(engineAfterUpdate, len) + '\n' +
                named(entityAdd, len) + '\n' +
                named(entityRemove, len) + '\n';

    }


    private String named(NamedData namedData, int columnLength){
        long avgNano;
        String avg;
        String min;
        String max;
        String framePart;

        if (namedData.calls == 0){
            avg = micro(0);
            min = micro(0);
            max = micro(0);
            framePart = framePercent(0);
        } else {
            avgNano = namedData.totalTime / namedData.calls;
            avg = micro(avgNano);
            min = micro(namedData.min);
            max = micro(namedData.max);
            framePart = framePercent(avgNano);
        }


        return addSpacesRight(namedData.name, columnLength) + " -> " +
                "calls: " + addSpacesRight(namedData.calls + ", ", 7) +
                "avg: " + addSpacesRight(avg, 9) + "(" + framePart + "),  " +
                "min: " + addSpacesRight(min + ", ", 10) +
                "max: " + max;
    }

    private int maxLength(NamedData... datas){
        int max = 0;
        for (NamedData data : datas) {
            if (data.name.length() > max){
                max = data.name.length();
            }
        }
        return max;
    }


    private String avg(long totalNano, int calls){
        return micro(totalNano / calls);
    }

    NumberFormat percentFormat = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    NumberFormat mcroFormat = new DecimalFormat("#0.0", new DecimalFormatSymbols(Locale.ENGLISH));
    private String micro(long nano){
        double microseconds = ((double) nano) / 1000;
        if (microseconds <= 1) {
            return mcroFormat.format(microseconds) + " us";
        } else {
            return Math.round(microseconds) + " us";
        }
    }

    /**
     * Percent of frame time (Considering FPS = 60)
     */
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
        return builder.append(s).toString();
    }

    public static String floatFormatted(float f, int numbersAfterComma){
        return String.format(Locale.ENGLISH, "%.0"+ numbersAfterComma + "f", f);
    }
}
