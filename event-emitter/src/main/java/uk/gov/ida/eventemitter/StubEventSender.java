package uk.gov.ida.eventemitter;

import com.amazonaws.Response;

public class StubEventSender implements EventSender {

    @Override
    public void sendAuthenticated(final Event event, final String encryptedEvent) {
        System.out.println(String.format(
                "Event ID: %s, Timestamp: %s, Event Type: %s, Event String: %s",
                event.getEventId(),
                event.getTimestamp(),
                event.getEventType(),
                encryptedEvent
        ));

    }

}
