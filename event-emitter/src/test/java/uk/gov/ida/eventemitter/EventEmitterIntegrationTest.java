package uk.gov.ida.eventemitter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.amazonaws.regions.Regions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Injector;
import httpstub.HttpStubRule;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import uk.gov.ida.eventemitter.utils.HttpResponse;
import uk.gov.ida.eventemitter.utils.TestDecrypter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.ida.eventemitter.EventEmitterTestHelper.ACCESS_KEY_ID;
import static uk.gov.ida.eventemitter.EventEmitterTestHelper.ACCESS_SECRET_KEY;
import static uk.gov.ida.eventemitter.EventEmitterTestHelper.AUDIT_EVENTS_API_RESOURCE;
import static uk.gov.ida.eventemitter.EventEmitterTestHelper.AUDIT_EVENTS_API_RESOURCE_INVALID;
import static uk.gov.ida.eventemitter.EventEmitterTestHelper.KEY;
import static uk.gov.ida.eventemitter.EventMessageBuilder.anEventMessage;

@RunWith(MockitoJUnitRunner.class)
public class EventEmitterIntegrationTest {

    private static final boolean CONFIGURATION_ENABLED = true;
    private static final String PID_VALUE = "pid value";
    private static final String IDENTIY_PROVIDER_ENTITY_ID = "identiy provider entity id";
    private static final Sha256Util SHA_256_UTIL = new Sha256Util();
    private static final Event EVENT = anEventMessage().build();

    private static TestAppender testAppender = new TestAppender();
    private static Injector injector = null;
    private static EventEmitter eventEmitter;
    private static ObjectMapper objectMapper;

    @ClassRule
    public static HttpStubRule apiGatewayStub = new HttpStubRule();

    @BeforeClass
    public static void setUp() {

        injector = EventEmitterTestHelper.createTestConfiguration(CONFIGURATION_ENABLED,
                ACCESS_KEY_ID,
                ACCESS_SECRET_KEY,
                Regions.EU_WEST_2,
                URI.create(apiGatewayStub.baseUri().build().toString() + AUDIT_EVENTS_API_RESOURCE));

        eventEmitter = injector.getInstance(EventEmitter.class);
        objectMapper = injector.getInstance(ObjectMapper.class);
    }

    @Before
    public void setUpLogAppender() {
        testAppender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.addAppender(testAppender);
        apiGatewayStub.reset();
    }

    @After
    public void tearDownLogAppender() {
        testAppender.stop();
        TestAppender.events.clear();
        Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        logger.detachAppender(testAppender);
        apiGatewayStub.reset();
    }

    @Test
    public void shouldEncryptMessageUsingEventEncrypterAndSendToAPIGateway() throws Exception {

        final Event expectedEvent = anEventMessage().withDetailsField(EventDetailsKey.idp_entity_id, IDENTIY_PROVIDER_ENTITY_ID)
                                                    .withDetailsField(EventDetailsKey.pid, PID_VALUE)
                                                    .build();
        apiGatewayStub.register(AUDIT_EVENTS_API_RESOURCE, HttpResponse.HTTP_200.getStatusCode());

        eventEmitter.record(expectedEvent);

        final String message = new String(apiGatewayStub.getLastRequest().getEntityBytes());
        final TestDecrypter<EventMessage> decrypter = new TestDecrypter(KEY, injector.getInstance(ObjectMapper.class));
        final Event actualEvent = decrypter.decrypt(message, EventMessage.class);
        final Map<EventDetailsKey, String> expectedDetails = actualEvent.getDetails();
        final Map<EventDetailsKey, String> actualDetails = actualEvent.getDetails();

        assertThat(actualEvent.getEventId()).isEqualTo(expectedEvent.getEventId());
        assertThat(actualEvent.getTimestamp()).isEqualTo(expectedEvent.getTimestamp());
        assertThat(actualEvent.getEventType()).isEqualTo(expectedEvent.getEventType());
        assertThat(actualEvent.getOriginatingService()).isEqualTo(expectedEvent.getOriginatingService());
        assertThat(actualEvent.getSessionId()).isEqualTo(expectedEvent.getSessionId());
        assertThatDetailsAreSameExceptPidIsHashed(expectedDetails, actualDetails);
        assertThat(TestAppender.events.get(0).toString()).contains(objectMapper.writeValueAsString(expectedEvent));
        assertThat(TestAppender.events.get(1).toString()).contains(
                String.format("Sent Event Message [Event Id: %s]",
                        expectedEvent.getEventId()));
    }

    @Test
    public void shouldFailSilentlyWithIncorrectResource() throws JsonProcessingException {

        apiGatewayStub.register(AUDIT_EVENTS_API_RESOURCE_INVALID, HttpResponse.HTTP_404.getStatusCode());

        eventEmitter.record(EVENT);

        assertThat(TestAppender.events.get(0).toString()).contains(objectMapper.writeValueAsString(EVENT));
        assertThat(
                TestAppender.events.get(1).toString()).contains(
                String.format("Failed to send a message [Event Id: %s] to the api gateway. Status: %s Error Message: %s",
                        EVENT.getEventId(),
                        HttpResponse.HTTP_404.getStatusCode(),
                        HttpResponse.HTTP_404.getStatusText()));

    }

    @Test
    public void shouldFailSilentlyWithUnauthorized() throws JsonProcessingException {

        apiGatewayStub.register(AUDIT_EVENTS_API_RESOURCE, HttpResponse.HTTP_403.getStatusCode());

        eventEmitter.record(EVENT);

        assertThat(TestAppender.events.get(0).toString()).contains(objectMapper.writeValueAsString(EVENT));
        assertThat(
                TestAppender.events.get(1).toString()).contains(
                String.format("Failed to send a message [Event Id: %s] to the api gateway. Status: %s Error Message: %s",
                        EVENT.getEventId(),
                        HttpResponse.HTTP_403.getStatusCode(),
                        HttpResponse.HTTP_403.getStatusText()));

    }

    @Test
    public void shouldFailSilentlyWithTimeout() throws JsonProcessingException {

        apiGatewayStub.register(AUDIT_EVENTS_API_RESOURCE, HttpResponse.HTTP_504.getStatusCode());

        eventEmitter.record(EVENT);

        assertThat(TestAppender.events.get(0).toString()).contains(objectMapper.writeValueAsString(EVENT));
        assertThat(
                TestAppender.events.get(1).toString()).contains(
                String.format("Failed to send a message [Event Id: %s] to the api gateway. Status: %s Error Message: %s",
                        EVENT.getEventId(),
                        HttpResponse.HTTP_504.getStatusCode(),
                        HttpResponse.HTTP_504.getStatusText()));

    }

    @Test(expected = Test.None.class)
    public void shouldFailSilentlyWithNullEvent() {

        eventEmitter.record(null);

        assertThat(
                TestAppender.events.get(0).toString()).contains(
                "[ERROR] Unable to send a message due to event containing null value.");

    }

    private static class TestAppender extends AppenderBase<ILoggingEvent> {

        public static List<ILoggingEvent> events = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {

            if (eventObject.getLevel() != Level.DEBUG) {
                events.add(eventObject);
            }
        }
    }

    private void assertThatDetailsAreSameExceptPidIsHashed(final Map<EventDetailsKey, String> expectedDetails,
                                                           final Map<EventDetailsKey, String> actualDetails) {
        assertThat(actualDetails).isNotNull();
        for (EventDetailsKey e : expectedDetails.keySet()) {
            assertThat(actualDetails.containsKey(e)).isTrue();
            if (e.equals(EventDetailsKey.pid)) {
                assertThat(actualDetails.get(EventDetailsKey.pid))
                .isEqualTo(SHA_256_UTIL.hash(IDENTIY_PROVIDER_ENTITY_ID, PID_VALUE));
            } else {
                assertThat(actualDetails.get(e)).isEqualTo(expectedDetails.get(e));
            }
        }
    }
}
