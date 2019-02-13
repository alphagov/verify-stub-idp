package uk.gov.ida.eventemitter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventMessageBuilderTest {

    private static final DateTime NOW = DateTime.now(DateTimeZone.UTC);
    private static final DateTime TIMESTAMP = DateTime.parse("2018-06-28T10:50:24+00:00");

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(NOW.getMillis());
    }

    @After
    public void tearDown() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void anEventMessage() {
        assertThat(EventMessageBuilder.anEventMessage()).isInstanceOf(EventMessageBuilder.class);
    }

    @Test
    public void build() {
        final Map<EventDetailsKey, String> details= new HashMap<>();
        details.put(EventDetailsKey.message, "Session error");
        details.put(EventDetailsKey.error_id, "7d20e892-2f01-4abc-b575-77b9f93c316z");

        final Event eventMessage = EventMessageBuilder.anEventMessage().build();

        assertThat(eventMessage.getEventId()).isEqualTo(UUID.fromString("7d20e892-2f01-4abc-b575-77b9f93c316f"));
        assertThat(eventMessage.getEventType()).isEqualTo("session_event");
        assertThat(eventMessage.getTimestamp()).isEqualTo(NOW);
        assertThat(eventMessage.getOriginatingService()).isEqualTo("test");
        assertThat(eventMessage.getSessionId()).isEqualTo("f81176c8-24f7-4622-ac61-7ebbc978bcdb");
        assertThat(eventMessage.getDetails()).isEqualTo(details);
    }

    @Test
    public void shouldUpdateEventId() {
        final UUID eventId = UUID.randomUUID();
        final Event eventMessage = EventMessageBuilder.anEventMessage().withEventId(eventId).build();

        assertThat(eventMessage.getEventId()).isEqualTo(eventId);
    }

    @Test
    public void shouldUpdateTimestamp() {
        final Event eventMessage = EventMessageBuilder.anEventMessage().withTimestamp(TIMESTAMP).build();

        assertThat(eventMessage.getTimestamp()).isEqualTo(TIMESTAMP);
    }

    @Test
    public void shouldUpdateEventType() {
        final String eventType = "special event type";
        final Event eventMessage = EventMessageBuilder.anEventMessage().withEventType(eventType).build();

        assertThat(eventMessage.getEventType()).isEqualTo(eventType);
    }

    @Test
    public void shouldUpdateOriginatingService() {
        final String originatingService = "service";
        final Event eventMessage = EventMessageBuilder.anEventMessage().withOriginatingService(originatingService).build();

        assertThat(eventMessage.getOriginatingService()).isEqualTo(originatingService);
    }

    @Test
    public void shouldUpdateSessionId() {
        final String sessionId = "session id";
        final Event eventMessage = EventMessageBuilder.anEventMessage().withSessionId(sessionId).build();

        assertThat(eventMessage.getSessionId()).isEqualTo(sessionId);
    }

    @Test
    public void shouldUpdateDetails() {
        final EnumMap<EventDetailsKey, String> details= new EnumMap<>(EventDetailsKey.class);
        details.put(EventDetailsKey.message, "Test");
        details.put(EventDetailsKey.country_code, "GB");

        final Event eventMessage = EventMessageBuilder.anEventMessage().withDetails(details).build();

        assertThat(eventMessage.getDetails()).isEqualTo(details);
    }
}
