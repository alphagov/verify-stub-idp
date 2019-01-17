package uk.gov.ida.stub.idp.services;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.factories.MatchingDatasetFactoryTest;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdpUserServiceTest {

    private final String RELAY_STATE = "relayState";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String IDP_NAME = "an idp name";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private IdpUserService idpUserService;

    @Mock
    private IdpSessionRepository sessionRepository;
    @Mock
    private Idp idp;
    @Mock
    private IdaAuthnRequestFromHub idaAuthnRequestFromHubOptional;
    @Mock
    private IdpStubsRepository idpStubsRepository;

    @Before
    public void createResource() {
        idpUserService = new IdpUserService(sessionRepository, idpStubsRepository);
    }

    @Test
    public void shouldBuildSuccessResponse() throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        Optional<DatabaseIdpUser> idpUserOptional = Optional.ofNullable(MatchingDatasetFactoryTest.completeUser);
        when(idp.getUser(USERNAME, PASSWORD)).thenReturn(idpUserOptional);
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHubOptional, RELAY_STATE, null, null, null, null, null, null)));

        idpUserService.attachIdpUserToSession(IDP_NAME, USERNAME, PASSWORD, SESSION_ID);

        ArgumentCaptor<IdpSession> argumentCaptor = ArgumentCaptor.forClass(IdpSession.class);
        verify(sessionRepository, times(1)).updateSession(eq(SESSION_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getIdpUser()).isEqualTo(idpUserOptional);
    }

    @Test
    public void shouldHaveStatusSuccessResponseWhenUserRegisters() throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {
        IdpSession session = new IdpSession(SessionId.createNewSessionId(), idaAuthnRequestFromHubOptional, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty(), Optional.empty(), null);
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new IdpSession(SESSION_ID, idaAuthnRequestFromHubOptional, RELAY_STATE, null, null, null, null, null, null)));
        when(idpStubsRepository.getIdpWithFriendlyId(IDP_NAME)).thenReturn(idp);
        when(idp.userExists(USERNAME)).thenReturn(false);
        when(idp.createUser(any(), any(), any(), any(), any(), any(), any(), eq(USERNAME), eq(PASSWORD), any())).thenReturn(mock(DatabaseIdpUser.class));
        when(sessionRepository.createSession(session)).thenReturn(SESSION_ID);

        idpUserService.createAndAttachIdpUserToSession(IDP_NAME, "bob", "jones", "address line 1", "address line 2", "address town", "address postcode", AuthnContext.LEVEL_2, "2000-01-01", USERNAME, "password", SESSION_ID);

        verify(sessionRepository, times(1)).updateSession(eq(SESSION_ID), any());
    }
}
