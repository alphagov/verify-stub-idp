package uk.gov.ida.eventemitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.eventemitter.utils.TestDecrypter;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

public class EventEncrypterTest {

    private static final byte[] KEY = "aesEncryptionKey".getBytes();

    private Event event;
    private EventEncrypter eventEncrypter;
    private TestDecrypter<Event> decrypter;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        event = anEventMessage().build();

        mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());

        eventEncrypter = new EventEncrypter(KEY);
        decrypter = new TestDecrypter<>(KEY, mapper);
    }

    @Test
    public void shouldEncryptEvent() throws Exception {
        final String encryptedEvent = eventEncrypter.encrypt(event);
        String decryptedEvent = decrypter.decrypt(encryptedEvent);
        assertThat(mapper.readValue(decryptedEvent, EventMessage.class)).isEqualTo(event);

        JSONObject jsonObject = new JSONObject(decryptedEvent);
        assertThat(jsonObject.getLong("timestamp")).isEqualTo(event.getTimestamp().getMillis());
    }

}
