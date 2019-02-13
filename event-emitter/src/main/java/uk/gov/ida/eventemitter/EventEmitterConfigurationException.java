package uk.gov.ida.eventemitter;

import static java.lang.String.format;

public class EventEmitterConfigurationException extends RuntimeException {
    public EventEmitterConfigurationException(String message, Exception e) {
        super(format("%s: %s", message, e.getMessage()));
    }
}
