package uk.gov.ida.eventemitter;

import org.joda.time.DateTime;

import java.util.EnumMap;
import java.util.UUID;

public interface Event {

    UUID getEventId();

    DateTime getTimestamp();

    String getEventType();

    EnumMap<EventDetailsKey,String> getDetails();

    String getOriginatingService();

    String getSessionId();
}
