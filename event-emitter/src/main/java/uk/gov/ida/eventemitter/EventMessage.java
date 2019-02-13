package uk.gov.ida.eventemitter;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.concurrent.Immutable;
import java.util.EnumMap;
import java.util.Objects;
import java.util.UUID;

@Immutable
public final class EventMessage implements Event {

    @JsonProperty
    private UUID eventId;

    @JsonProperty
    private DateTime timestamp;

    @JsonProperty
    private String eventType;

    @JsonProperty
    private String originatingService;

    @JsonProperty
    private String sessionId;

    @JsonProperty
    private EnumMap<EventDetailsKey, String> details;

    private EventMessage() {
    }

    public EventMessage(final UUID eventId,
                 final DateTime timestamp,
                 final String eventType,
                 final String originatingService,
                 final String sessionId,
                 final EnumMap<EventDetailsKey, String> details) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.originatingService = originatingService;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.details = details;
    }

    public EventMessage(final String originatingService,
                 final String sessionId,
                 final String eventType,
                 final EnumMap<EventDetailsKey, String> details) {
        this(
            UUID.randomUUID(),
            DateTime.now(DateTimeZone.UTC),
            originatingService,
            sessionId,
            eventType,
            details);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public DateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getOriginatingService() {
        return originatingService;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public EnumMap<EventDetailsKey, String> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Event{");
        sb.append("eventId=").append(eventId);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", eventType='").append(eventType).append('\'');
        sb.append(", originatingService='").append(originatingService).append('\'');
        sb.append(", sessionId='").append(sessionId).append('\'');
        sb.append(", details=").append(details);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventMessage event = (EventMessage) o;
        return Objects.equals(eventId, event.eventId) &&
               Objects.equals(timestamp, event.timestamp) &&
               Objects.equals(eventType, event.eventType) &&
               Objects.equals(originatingService, event.originatingService) &&
               Objects.equals(sessionId, event.sessionId) &&
               Objects.equals(details, event.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, timestamp, eventType, originatingService, sessionId, details);
    }
}
