package uk.gov.ida.eventemitter;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

public class StubEventSenderTest {

    private static final String ENCRYPTED_EVENT = "encryptedEvent";

    private StubEventSender stubEventSender;
    private Event event;

    @Before
    public void setUp() {
        stubEventSender = new StubEventSender();
    }

    @Test
    public void shouldWriteEventDetailsToStandardOutput() throws IOException {
        event = anEventMessage().build();

        try (ByteArrayOutputStream outContent = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outContent)) {
            System.setOut(printStream);
            stubEventSender.sendAuthenticated(event, ENCRYPTED_EVENT);
            System.setOut(System.out);

            assertThat(outContent.toString())
                    .containsOnlyOnce(String.format(
                            "Event ID: %s, Timestamp: %s, Event Type: %s, Event String: %s\n",
                            event.getEventId().toString(),
                            event.getTimestamp(),
                            event.getEventType(),
                            ENCRYPTED_EVENT
                    ));
        }
    }

    @Test
    public void shouldNotThrowErrorsIfInputsAreNull() throws IOException {
        event = new EventMessage(null, null, null, null, null, null);

        try (ByteArrayOutputStream outContent = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outContent)) {
            System.setOut(printStream);
            stubEventSender.sendAuthenticated(event, "null");
            System.setOut(System.out);

            assertThat(outContent.toString())
                    .containsOnlyOnce("Event ID: null, Timestamp: null, Event Type: null, Event String: null\n");
        }
    }
}
