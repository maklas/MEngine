package ru.maklas.mengine.performance_new;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.mengine.performance_new.results.EventData;

public class EventAccumulator {

    private ObjectMap<Class, EventData> map = new ObjectMap<Class, EventData>();

    public void saveEvent(Class eventClass, long totalTime, long internalTime){
        EventData eventData = map.get(eventClass);
        if (eventData == null) {
            eventData = new EventData(eventClass);
            map.put(eventClass, eventData);
        }

        eventData.calls++;
        eventData.totalTime += totalTime;
        eventData.selfTime += internalTime;
    }


    public Array<EventData> get(){
        return map.values().toArray();
    }

}
