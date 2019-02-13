package uk.gov.ida.eventemitter;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

public class StubEncrypterTest {

    @Test
    public void shouldReturnEncryptedEvent() {
        final StubEncrypter encrypter = new StubEncrypter();
        final Event event = anEventMessage().build();

        final String actualValue = encrypter.encrypt(event);

        assertThat(actualValue).isEqualTo(String.format("Encrypted Event Id %s", event.getEventId().toString()));
    }
}
