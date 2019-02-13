package uk.gov.ida.eventemitter;

import com.google.inject.Injector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;


public class EventEmitterWithDisabledConfigTest {
    private static final boolean CONFIGURATION_ENABLED = false;

    private static Injector injector;

    @BeforeClass
    public static void setUp() {

        injector = EventEmitterTestHelper.createTestConfiguration(CONFIGURATION_ENABLED,
                null,
                null,
                null,
                null
        );

    }

    @Test
    public void shouldReturnStubbedClientWhenConfigIsDisabled() {
        final EventEmitter eventEmitter = injector.getInstance(EventEmitter.class);
        final Map<String, String> details = new HashMap<>();
        final Event event = anEventMessage().build();

        eventEmitter.record(event);

        assertThat(eventEmitter.getClass().equals(StubEventSender.class));
    }
}
