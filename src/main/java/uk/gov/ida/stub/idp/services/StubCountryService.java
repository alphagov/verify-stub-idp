package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.EidasUser;
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

import javax.inject.Inject;
import java.util.Optional;

public class StubCountryService {

    private final StubCountryRepository stubCountryRepository;
    private final SessionRepository<EidasSession> sessionRepository;

    @Inject
    public StubCountryService(StubCountryRepository stubCountryRepository, SessionRepository<EidasSession> sessionRepository) {
        this.stubCountryRepository = stubCountryRepository;
        this.sessionRepository = sessionRepository;
    }

    public void attachStubCountryToSession(String schemeName, String username, String password, EidasSession session) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(schemeName);
        Optional<DatabaseEidasUser> user = stubCountry.getUser(username, password);
        attachEidasUserToSession(user, session);
    }

    public void createAndAttachIdpUserToSession(String countryName,
                                                String username, String password,
                                                EidasSession idpSessionId,
                                                String firstName,
                                                String surname,
                                                String dob,
                                                AuthnContext levelOfAssurance) throws InvalidSessionIdException, InvalidUsernameOrPasswordException, InvalidDateException, IncompleteRegistrationException, UsernameAlreadyTakenException {
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(countryName);
        DatabaseEidasUser user = createEidasUserInStubCountry(username, password, stubCountry, firstName, surname, dob, levelOfAssurance);
        attachEidasUserToSession(Optional.of(user), idpSessionId);
    }

    private DatabaseEidasUser createEidasUserInStubCountry(String username, String password,
                                                           StubCountry stubCountry, String firstName, String surname, String dob,
                                                           AuthnContext levelOfAssurance) throws InvalidDateException, IncompleteRegistrationException, UsernameAlreadyTakenException {

        if (!isMandatoryDataPresent(firstName, surname, dob, username, password)) {
            throw new IncompleteRegistrationException();
        }

        LocalDate parsedDateOfBirth;
        try {
            parsedDateOfBirth = LocalDate.parse(dob, DateTimeFormat.forPattern("yyyy-MM-dd"));
        } catch (IllegalArgumentException e) {
            throw new InvalidDateException();
        }

        boolean usernameAlreadyTaken = stubCountry.userExists(username);
        if (usernameAlreadyTaken) {
            throw new UsernameAlreadyTakenException();
        }

        return stubCountry.createUser(username, password, createMdsValue(firstName), createMdsValue(surname), createMdsValue(parsedDateOfBirth), levelOfAssurance);

    }

    private void attachEidasUserToSession(Optional<DatabaseEidasUser> user, EidasSession session) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        if (!user.isPresent()) {
            throw new InvalidUsernameOrPasswordException();
        }
        EidasUser eidasUser = createEidasUser(user);

        session.setEidasUser(eidasUser);

        if (!session.getEidasUser().isPresent()) {
            throw new InvalidSessionIdException();
        }

        sessionRepository.updateSession(session.getSessionId(), session);
    }

    private EidasUser createEidasUser(Optional<DatabaseEidasUser> user) {

        EidasUser eidasUser = new EidasUser(
                user.get().getFirstname().getValue(),
                user.get().getSurname().getValue(),
                user.get().getPersistentId(),
                user.get().getDateOfBirth().getValue(),
                Optional.empty(),
                Optional.empty()
        );

        return eidasUser;
    }

    private <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return new MatchingDatasetValue<>(value, null, null, true);
    }

    private boolean isMandatoryDataPresent(String... args) {
        for (String arg : args) {
            if (arg == null || arg.trim().length() == 0) {
                return false;
            }
        }

        return true;
    }
}
