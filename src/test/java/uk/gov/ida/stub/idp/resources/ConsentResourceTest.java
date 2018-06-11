package uk.gov.ida.stub.idp.resources;

import com.google.common.collect.ImmutableList;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.resources.idp.ConsentResource;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.ConsentView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsentResourceTest {

    private static final String RELAY_STATE = "relay";

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    @Mock
    private IdpStubsRepository idpStubsRepository;
    @Mock
    private SessionRepository<IdpSession> sessionRepository;
    @Mock
    private IdaAuthnRequestFromHub idaAuthnRequestFromHub;
    @Mock
    private SuccessAuthnResponseService successAuthnResponseService;
    @Mock
    private NonSuccessAuthnResponseService nonSuccessAuthnResponseService;

    private ConsentResource consentResource;

    @Before
    public void setUp() {
        consentResource = new ConsentResource(idpStubsRepository, sessionRepository, successAuthnResponseService, nonSuccessAuthnResponseService, new SamlResponseRedirectViewFactory());
    }

    private final String idpName = "idpName";
    private final Idp idp = new Idp(idpName, "Test Idp", "test-idp-asset-id", true, TestEntityIds.STUB_IDP_ONE, null);

    @Test
    public void shouldWarnUserIfLOAIsTooLow() {
        final SessionId idpSessionId = SessionId.createNewSessionId();

        IdpSession session = new IdpSession(idpSessionId, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null);
        session.setIdpUser(newUser(AuthnContext.LEVEL_1));
        when(sessionRepository.get(idpSessionId)).thenReturn(Optional.ofNullable(session));

        when(idaAuthnRequestFromHub.getLevelsOfAssurance()).thenReturn(Collections.singletonList(uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_2));
        when(idpStubsRepository.getIdpWithFriendlyId(idpName)).thenReturn(idp);

        final Response response = consentResource.get(idpName, idpSessionId);

        final ConsentView consentView = (ConsentView) response.getEntity();
        assertThat(consentView.isUserLOADidNotMatch()).isTrue();
    }

    @Test
    public void shouldWarnUserIfLOAIsTooLowWhenMultipleValuesPresent() {
        final SessionId idpSessionId = SessionId.createNewSessionId();

        IdpSession session = new IdpSession(idpSessionId, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null);
        session.setIdpUser(newUser(AuthnContext.LEVEL_1));
        when(sessionRepository.get(idpSessionId)).thenReturn(Optional.ofNullable(session));

        when(idaAuthnRequestFromHub.getLevelsOfAssurance()).thenReturn(ImmutableList.of(uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_1, AuthnContext.LEVEL_2));
        when(idpStubsRepository.getIdpWithFriendlyId(idpName)).thenReturn(idp);

        final Response response = consentResource.get(idpName, idpSessionId);

        final ConsentView consentView = (ConsentView) response.getEntity();
        assertThat(consentView.isUserLOADidNotMatch()).isFalse();
    }

    @Test
    public void shouldNotWarnUserIfLOAIsOk() {
        final SessionId idpSessionId = SessionId.createNewSessionId();

        IdpSession session = new IdpSession(idpSessionId, idaAuthnRequestFromHub, RELAY_STATE, null, null, null, null);
        session.setIdpUser(newUser(AuthnContext.LEVEL_2));
        when(sessionRepository.get(idpSessionId)).thenReturn(Optional.ofNullable(session));

        when(idaAuthnRequestFromHub.getLevelsOfAssurance()).thenReturn(Collections.singletonList(uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_2));
        when(idpStubsRepository.getIdpWithFriendlyId(idpName)).thenReturn(idp);

        final Response response = consentResource.get(idpName, idpSessionId);
        final ConsentView consentView = (ConsentView) response.getEntity();

        assertThat(consentView.isUserLOADidNotMatch()).isFalse();
    }

    private Optional<DatabaseIdpUser> newUser(AuthnContext levelOfAssurance) {
        return Optional.ofNullable(new DatabaseIdpUser(
                idpName + "-new",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Griffin")),
                Optional.ofNullable(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1983-06-21"))),
                Collections.singletonList(new AddressFactory().create(Collections.singletonList("Lion's Head Inn"), "1A 2BC", null, null, null, null, true)),
                levelOfAssurance));
    }

    private static <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return (value == null) ? null : new MatchingDatasetValue<>(value, null, null, true);
    }
}
