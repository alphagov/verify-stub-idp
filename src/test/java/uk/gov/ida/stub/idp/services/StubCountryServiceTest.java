package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StubCountryServiceTest {

    private static final String SCHEME_ID = "scheme-id";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private final SessionId SESSION_ID = SessionId.createNewSessionId();

    private StubCountryService stubCountryService;

    private Optional<DatabaseIdpUser> user;

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

    private Optional<DatabaseIdpUser> newUser() {
        return Optional.ofNullable(new DatabaseIdpUser(
                "stub-country",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Griffin")),
                Optional.ofNullable(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1983-06-21"))),
                Collections.singletonList(new AddressFactory().create(Arrays.asList("10 London Road", "London"), "1A 2BC", null, null, null, null, true)),
                AuthnContext.LEVEL_2));
    }

    private static <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return (value == null) ? null : new MatchingDatasetValue<>(value, null, null, true);
    }
}

