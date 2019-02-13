package uk.gov.ida.eventemitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

@RunWith(MockitoJUnitRunner.class)
public class EventEmitterTest {

    private static final String ENCRYPTED_EVENT = "encrypted event";

    private EventEmitter eventEmitter;
    private Event event;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EventHasher eventHasher;

    @Mock
    private EventEncrypter eventEncrypter;

    @Mock
    private EventSender eventSender;

    @Before
    public void setUp() throws Exception {
        event = anEventMessage().build();
        when(eventHasher.replacePersistentIdWithHashedPersistentId(event)).thenReturn(event);
        when(eventEncrypter.encrypt(event)).thenReturn(ENCRYPTED_EVENT);

        eventEmitter = new EventEmitter(objectMapper, eventHasher, eventEncrypter, eventSender);
    }

    @Test
    public void shouldEncryptAndSendEncryptedEventToSqs() throws Exception {
        eventEmitter.record(event);

        verify(objectMapper).writeValueAsString(event);
        verify(eventHasher).replacePersistentIdWithHashedPersistentId(event);
        verify(eventEncrypter).encrypt(event);
        verify(eventSender).sendAuthenticated(event, ENCRYPTED_EVENT);
    }

    @Test
    public void shouldLogErrorAfterFailingToEncrypt() throws Exception {
        final String errorMessage = "Failed to encrypt.";
        when(eventEncrypter.encrypt(event)).thenThrow(new EventEncryptionException(String.format(
                "Failed to send a message [Event Id: %s] to the queue. Error Message: %s\nEvent Message: null\n",
                event.getEventId().toString(),
                errorMessage)));

        try (ByteArrayOutputStream errorContent = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(errorContent)) {
            System.setOut(printStream);
            eventEmitter.record(event);
            System.setOut(System.out);

            assertThat(errorContent.toString()).contains(String.format(
                    "Failed to send a message [Event Id: %s] to the queue. Error Message: %s\nEvent Message: null\n",
                    event.getEventId().toString(),
                    errorMessage
            ));
        }
    }

    @Test
    public void shouldLogErrorWhenEventIsNull() throws IOException {
        try (ByteArrayOutputStream errorContent = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(errorContent)) {
            System.setOut(printStream);
            eventEmitter.record(null);
            System.setOut(System.out);

            assertThat(errorContent.toString()).contains("Unable to send a message due to event containing null value.\n");
        }
    }
}
