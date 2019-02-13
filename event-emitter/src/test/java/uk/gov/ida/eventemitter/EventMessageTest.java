package uk.gov.ida.eventemitter;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventMessageTest {

    private static final UUID EVENT_ID = UUID.fromString("a10ed4f2-3bcf-4e0a-8ded-f21726c62830");
    private static final DateTime TIMESTAMP = DateTime.parse("2018-06-28T10:50:24+00:00");
    private static final String EVENT_TYPE = "myEventType";
    private static final String ORIGINATING_SERVICE = "anyService";
    private static final String SESSION_ID = "f89566c8-24f7-4622-fg61-7ebbc978bcdb";
    private static final EnumMap<EventDetailsKey, String> details = new EnumMap<>(EventDetailsKey.class);
    private static final String PID = "pid12345678";
    private static final String MESSAGE = "Hello Verifiers!!!";

    private EventMessage eventMessage;

    @Before
        public void setUp() {
        details.put(EventDetailsKey.pid, PID);
        details.put(EventDetailsKey.message, MESSAGE);
        eventMessage =
            EventMessageBuilder.anEventMessage()
                               .withEventId(EVENT_ID)
                               .withTimestamp(TIMESTAMP)
                               .withEventType(EVENT_TYPE)
                               .withOriginatingService(ORIGINATING_SERVICE)
                               .withSessionId(SESSION_ID)
                               .withDetails(details)
                               .build();
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(EventMessage.class).verify();
    }

    @Test
    public void testConstructorSetsProperties() {
        assertThat(eventMessage.getEventId()).isEqualTo(EVENT_ID);
        assertThat(eventMessage.getTimestamp()).isEqualTo(TIMESTAMP);
        assertThat(eventMessage.getEventType()).isEqualTo(EVENT_TYPE);
        assertThat(eventMessage.getOriginatingService()).isEqualTo(ORIGINATING_SERVICE);
        assertThat(eventMessage.getSessionId()).isEqualTo(SESSION_ID);
        assertThat(eventMessage.getDetails()).size().isEqualTo(details.size());
        assertThat(eventMessage.getDetails().get(EventDetailsKey.pid)).isEqualTo(PID);
        assertThat(eventMessage.getDetails().get(EventDetailsKey.message)).isEqualTo(MESSAGE);
    }
}
