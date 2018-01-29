package uk.gov.ida.stub.idp.services;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Arrays.asList;

public class IdpUserService {

    private final SessionRepository sessionRepository;
    private final IdpStubsRepository idpStubsRepository;

    @Inject
    public IdpUserService(
            SessionRepository sessionRepository,
            IdpStubsRepository idpStubsRepository) {

        this.sessionRepository = sessionRepository;
        this.idpStubsRepository = idpStubsRepository;
    }

    public void attachIdpUserToSession(String idpName, String username, String password, SessionId idpSessionId) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        Optional<IdpUser> user = idp.getUser(username, password);
        attachIdpUserToSession(user, idpSessionId);
    }

    public void attachIdpUserToSession(String idpName,
                                       String firstname, String surname,
                                       String addressLine1, String addressLine2, String addressTown, String addressPostCode,
                                       AuthnContext levelOfAssurance,
                                       String dateOfBirth,
                                       String username, String password,
                                       SessionId idpSessionId) throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        IdpUser user = createUserInIdp(firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode, levelOfAssurance, dateOfBirth, username, password, idp);
        attachIdpUserToSession(Optional.ofNullable(user), idpSessionId);
    }

    public void attachIdpUserToSession(Optional<IdpUser> user, SessionId idpSessionId) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {

        if (!user.isPresent()) {
            throw new InvalidUsernameOrPasswordException();
        }

        Optional<Session> session = sessionRepository.get(idpSessionId);

        if (!session.isPresent()) {
            throw new InvalidSessionIdException();
        }

        session.get().setIdpUser(user);
        sessionRepository.updateSession(session.get().getSessionId(), session.get());
    }

    private IdpUser createUserInIdp(String firstname, String surname, String addressLine1, String addressLine2, String addressTown, String addressPostCode, AuthnContext levelOfAssurance, String dateOfBirth, String username, String password, Idp idp) throws IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException {
        if (!isMandatoryDataPresent(firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode, dateOfBirth, username, password)) {
            throw new IncompleteRegistrationException();
        }

        LocalDate parsedDateOfBirth;
        try {
            parsedDateOfBirth = LocalDate.parse(dateOfBirth, DateTimeFormat.forPattern("yyyy-MM-dd"));
        } catch (IllegalArgumentException e) {
            throw new InvalidDateException();
        }

        boolean usernameAlreadyTaken = idp.userExists(username);
        if (usernameAlreadyTaken) {
            throw new UsernameAlreadyTakenException();
        }

        Address address = new Address(asList(addressLine1, addressLine2), addressPostCode, null, null, null, null, false);

        if ("LevelZeroUser".equals(username)) {
            levelOfAssurance = AuthnContext.LEVEL_X;
        }

        return idp.createUser(
                Optional.empty(),
                Collections.singletonList(createMdsValue(fromNullable(firstname))),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue(fromNullable(surname))),
                com.google.common.base.Optional.absent(),
                Collections.singletonList(createMdsValue(fromNullable(parsedDateOfBirth))),
                Collections.singletonList(address),
                username,
                password,
                levelOfAssurance);
    }

    private boolean isMandatoryDataPresent(String... args) {
        for (String arg : args) {
            if (arg == null || arg.trim().length() == 0) {
                return false;
            }
        }

        return true;
    }

    private static <T> SimpleMdsValue<T> createMdsValue(com.google.common.base.Optional<T> value) {
        return new SimpleMdsValue<>(value.get(), null, null, true);
    }

    public static IdpUser createRandomUser() {
        return new IdpUser(
                "tempuser",
                UUID.randomUUID().toString(),
                "ifitellyouthen...",
                Collections.singletonList(createSimpleMdsValue("firstname")),
                Collections.emptyList(),
                Collections.singletonList(createSimpleMdsValue("smith")),
                fromNullable(createSimpleMdsValue(Gender.FEMALE)),
                Collections.emptyList(),
                Collections.singletonList(
                        new Address(asList("line1", "line2"), "KT23 4XD", null, "fhfhf", DateTime.parse("2000-01-01"), DateTime.parse("2013-05-05"), false)
                ),
                AuthnContext.LEVEL_2);
    }

    private static <T> SimpleMdsValue<T> createSimpleMdsValue(T value) {
        return new SimpleMdsValue<>(value, DateTime.parse("2000-01-01"), DateTime.parse("2013-01-03"), false);
    }

    public void attachEidasUserToSession(String schemeName, String username, String password, SessionId sessionCookie) throws InvalidSessionIdException, InvalidUsernameOrPasswordException{
    }
}
