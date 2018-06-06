package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StubCountryServiceTest {

    private final String RELAY_STATE = "relayState";
    private static final String SCHEME_ID = "scheme-id";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();

    private StubCountryService stubCountryService;

    private Optional<DatabaseEidasUser> user;

    private EidasSession session;

    private EidasAuthnRequest eidasAuthnRequest;
    
    @Mock
    private SessionRepository<EidasSession> sessionRepository;

    @Mock
    private StubCountryRepository stubCountryRepository;

    @Mock
    private StubCountry stubCountry;

    @Before
    public void setUp(){
        when(stubCountryRepository.getStubCountryWithFriendlyId(SCHEME_ID)).thenReturn(stubCountry);
        stubCountryService = new StubCountryService(stubCountryRepository, sessionRepository);
        eidasAuthnRequest = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        session = new EidasSession(SESSION_ID, eidasAuthnRequest, null, null, null, Optional.empty(), Optional.empty());
        user = newUser();
    }

    @Test
    public void shouldAttachEidasToSession() throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        when(stubCountry.getUser(USERNAME, PASSWORD)).thenReturn(user);

        stubCountryService.attachStubCountryToSession(SCHEME_ID, USERNAME, PASSWORD, session);

        assertThat(session.getEidasUser().isPresent()).isTrue();
    }

    @Test(expected = InvalidUsernameOrPasswordException.class)
    public void shouldThrowExceptionWhenUserIsNotPresent() throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        user = Optional.empty();
        when(stubCountry.getUser(USERNAME, PASSWORD)).thenReturn(user);

        stubCountryService.attachStubCountryToSession(SCHEME_ID, USERNAME, PASSWORD, session);
    }

    @Test
    public void shouldHaveStatusSuccessResponseWhenUserRegisters() throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {
        EidasSession session = new EidasSession(SESSION_ID, eidasAuthnRequest, "test-relay-state", Arrays.asList(), Arrays.asList(), Optional.empty(), Optional.empty());
        when(sessionRepository.get(SESSION_ID)).thenReturn(Optional.ofNullable(new EidasSession(SESSION_ID, eidasAuthnRequest, RELAY_STATE, null, null, null, null)));
        when(stubCountryRepository.getStubCountryWithFriendlyId(SCHEME_ID)).thenReturn(stubCountry);
        when(stubCountry.createUser(eq(USERNAME), eq(PASSWORD), any(), any(), any(), any(), any(), any())).thenReturn(newUser().get());

        stubCountryService.createAndAttachIdpUserToSession(SCHEME_ID, USERNAME, "password", session, "bob", "bobNonLatin", "jones", "jonesNonLatin", "2000-01-01", AuthnContext.LEVEL_2);

        verify(sessionRepository, times(1)).updateSession(eq(SESSION_ID), any());
    }

    private Optional<DatabaseEidasUser> newUser() {
        return Optional.of(new DatabaseEidasUser("stub-country", UUID.randomUUID().toString(), "bar", createMdsValue("Jack"), Optional.of(createMdsValue("JackNonLatin")), createMdsValue("Griffin"), Optional.of(createMdsValue("GriffinNonLatin")), createMdsValue(LocalDate.parse("1983-06-21")), AuthnContext.LEVEL_2));
    }

    private static <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return (value == null) ? null : new MatchingDatasetValue<>(value, null, null, true);
    }
}

