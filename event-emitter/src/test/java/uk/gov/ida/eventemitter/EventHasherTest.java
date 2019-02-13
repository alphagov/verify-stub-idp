package uk.gov.ida.eventemitter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

@RunWith(MockitoJUnitRunner.class)
public class EventHasherTest {

    private static final String PID_VALUE = "pid value";
    private static final String IDENTITY_PROVIDER_ENTITY_ID = "identity provider entity id";
    private static final String HASHED_PID = "hashed-pid";

    @Mock
    private static Sha256Util sha256Util;

    private static EventHasher eventHasher;

    @Before
    public void setUp() {
        eventHasher = new EventHasher(sha256Util);
    }

    @Test
    public void shouldHashThePid() {
        when(sha256Util.hash(IDENTITY_PROVIDER_ENTITY_ID, PID_VALUE)).thenReturn(HASHED_PID);
        final Event event = anEventMessage().withDetailsField(EventDetailsKey.pid, PID_VALUE)
                                            .withDetailsField(EventDetailsKey.idp_entity_id, IDENTITY_PROVIDER_ENTITY_ID)
                                            .build();

        final Event actualEvent = eventHasher.replacePersistentIdWithHashedPersistentId(event);
        final Map<EventDetailsKey, String> actualDetails = actualEvent.getDetails();

        assertThat(actualDetails).isNotEmpty();
        assertThat(actualDetails.get(EventDetailsKey.pid)).isEqualTo(HASHED_PID);
    }

    @Test
    public void shouldContinueProcessingIfPidIsAbsent() {
        final Event event = anEventMessage().withDetailsField(EventDetailsKey.idp_entity_id, IDENTITY_PROVIDER_ENTITY_ID)
                                            .build();

        final Event actualEvent = eventHasher.replacePersistentIdWithHashedPersistentId(event);

        verify(sha256Util, never()).hash(Matchers.anyVararg());
        assertThat(actualEvent.getDetails().containsKey(EventDetailsKey.pid)).isEqualTo(false);
    }

    @Test
    public void shouldOverWritePidIfIdpEntityIdIsMissing() {
        final Event event = anEventMessage().withDetailsField(EventDetailsKey.pid, PID_VALUE)
                                            .build();

        final Event actualEvent = eventHasher.replacePersistentIdWithHashedPersistentId(event);
        final Map<EventDetailsKey, String> actualDetails = actualEvent.getDetails();

        verify(sha256Util, never()).hash(Matchers.anyVararg());
        assertThat(actualDetails).isNotEmpty();
        assertThat(actualDetails.get(EventDetailsKey.pid)).isEqualTo(EventHasher.PID_REMOVED);
    }
}
