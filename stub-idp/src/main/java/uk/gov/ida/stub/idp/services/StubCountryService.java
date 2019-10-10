package uk.gov.ida.stub.idp.services;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.EidasScheme;
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

    public void attachStubCountryToSession(EidasScheme eidasScheme, String username, String password, boolean signAssertions, EidasSession session) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme);
        Optional<DatabaseEidasUser> user = stubCountry.getUser(username, password);
        attachEidasUserToSession(user, session);
        setAssertionSigningIntention(signAssertions, session);
    }

    public void createAndAttachIdpUserToSession(EidasScheme eidasScheme,
                                                String username, String password,
                                                EidasSession idpSessionId,
                                                String firstName,
                                                String nonLatinFirstname,
                                                String surname,
                                                String nonLatinSurname,
                                                String dob,
                                                AuthnContext levelOfAssurance) throws InvalidSessionIdException, InvalidUsernameOrPasswordException, InvalidDateException, IncompleteRegistrationException, UsernameAlreadyTakenException {
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme);
        DatabaseEidasUser user = createEidasUserInStubCountry(
                username, password, stubCountry, firstName, nonLatinFirstname,
                surname, nonLatinSurname, dob, levelOfAssurance
        );
        attachEidasUserToSession(Optional.of(user), idpSessionId);
    }

    private DatabaseEidasUser createEidasUserInStubCountry(String username,
                                                           String password,
                                                           StubCountry stubCountry,
                                                           String firstName,
                                                           String nonLatinFirstname,
                                                           String surname,
                                                           String nonLatinSurname,
                                                           String dob,
                                                           AuthnContext levelOfAssurance)
            throws InvalidDateException, IncompleteRegistrationException, UsernameAlreadyTakenException {

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

        return stubCountry.createUser(
                username, password,
                createMdsValue(firstName), createOptionalMdsValue(nonLatinFirstname),
                createMdsValue(surname), createOptionalMdsValue(nonLatinSurname),
                createMdsValue(parsedDateOfBirth),
                levelOfAssurance);
    }

    private void setAssertionSigningIntention(boolean signAssertions, EidasSession session) {
        session.setSignAssertions(signAssertions);
        sessionRepository.updateSession(session.getSessionId(), session);
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

    private EidasUser createEidasUser(Optional<DatabaseEidasUser> optionalUser) {

        DatabaseEidasUser user = optionalUser.get();

        return new EidasUser(
                user.getFirstname().getValue(),
                getOptionalValue(user.getNonLatinFirstname()),
                user.getSurname().getValue(),
                getOptionalValue(user.getNonLatinSurname()),
                user.getPersistentId(),
                user.getDateOfBirth().getValue(),
                Optional.empty(),
                Optional.empty()
        );
    }

    private Optional<String> getOptionalValue(Optional<MatchingDatasetValue<String>> fieldValue) {
        return fieldValue.map(MatchingDatasetValue::getValue);
    }

    private <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return new MatchingDatasetValue<>(value, null, null, true);
    }

    private Optional<MatchingDatasetValue<String>> createOptionalMdsValue(String value) {
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }
        return Optional.of(new MatchingDatasetValue<>(value, null, null, true));
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
