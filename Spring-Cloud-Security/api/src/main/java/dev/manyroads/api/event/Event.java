package dev.manyroads.api.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

public class Event<K, T> {

    public enum Type{CREATE, DELETE}

    Type            eventType;
    K               eventKey;
    T               eventData;
    ZonedDateTime   eventTimeStamp;

    public Event() {
        this.eventType = null;
        this.eventKey = null;
        this.eventData = null;
        this.eventTimeStamp = null;
    }

    public Event(Type eventType, K eventKey, T eventData) {
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.eventData = eventData;
        this.eventTimeStamp = now();
    }

    public Type getEventType() {
        return eventType;
    }

    public K getEventKey() {
        return eventKey;
    }

    public T getEventData() {
        return eventData;
    }

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    public ZonedDateTime getEventTimeStamp() {
        return eventTimeStamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventType=" + eventType +
                ", eventKey=" + eventKey +
                ", eventData=" + eventData +
                ", eventTimeStamp=" + eventTimeStamp +
                '}';
    }
}
