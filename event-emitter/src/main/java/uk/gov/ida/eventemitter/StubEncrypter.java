package uk.gov.ida.eventemitter;

public class StubEncrypter implements Encrypter {

    @Override
    public String encrypt(final Event event)  {
        return String.format("Encrypted Event Id %s", event.getEventId().toString());
    }
}
