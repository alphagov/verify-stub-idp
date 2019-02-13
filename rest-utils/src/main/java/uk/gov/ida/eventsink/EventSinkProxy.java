package uk.gov.ida.eventsink;

import uk.gov.ida.eventemitter.Event;

public interface EventSinkProxy {

    void logHubEvent(Event eventSinkHubEvent);
}
