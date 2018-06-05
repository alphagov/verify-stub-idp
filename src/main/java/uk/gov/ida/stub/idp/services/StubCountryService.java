package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.EidasAddress;
import uk.gov.ida.stub.idp.domain.EidasUser;
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
        Optional<DatabaseIdpUser> user = stubCountry.getUser(username, password);
        attachEidasUserToSession(user, session);
    }

    public void createAndAttachIdpUserToSession(String countryName,
                                                String username, String password,
                                                EidasSession idpSessionId) throws InvalidSessionIdException, IncompleteRegistrationException, InvalidDateException, UsernameAlreadyTakenException, InvalidUsernameOrPasswordException {
//        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(countryName);
//        EidasUser user = createUserInIdp(username, password, stubCountry);
//        attachEidasUserToSession(Optional.ofNullable(user), idpSessionId);
    }

    private void attachEidasUserToSession(Optional<DatabaseIdpUser> user, EidasSession session) throws InvalidUsernameOrPasswordException, InvalidSessionIdException {
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

    private EidasUser createEidasUser(Optional<DatabaseIdpUser> user) {

        EidasUser eidasUser = new EidasUser(
                getEidasFirstnames(user),
                getEidasSurnames(user),
                getPersistentId(user),
                getEidasDateOfBirths(user),
                getEidasAddress(user),
                getGender(user)
        );

        return eidasUser;
    }

    private String getEidasFirstnames(Optional<DatabaseIdpUser> user) {
        return user.get().getFirstnames().get(0).getValue();
    }

    private String getEidasSurnames(Optional<DatabaseIdpUser> user) {
        return user.get().getSurnames().get(0).getValue();
    }

    private String getPersistentId(Optional<DatabaseIdpUser> user) {
        return user.get().getPersistentId();
    }

    private LocalDate getEidasDateOfBirths(Optional<DatabaseIdpUser> user) {
        return user.get().getDateOfBirths().get(0).getValue();
    }

    private Optional<EidasAddress> getEidasAddress(Optional<DatabaseIdpUser> user) {

        return user.get().getAddresses()
                .stream()
                .map(Optional::ofNullable)
                .findFirst()
                .map(t -> new EidasAddress(
                "",
                "",
                "",
                "",
                "",
                "",
                t.get().getLines().get(0),
                "",
                t.get().getPostCode().get()
                ));
    }

    private Optional<Gender> getGender(Optional<DatabaseIdpUser> user) {
        return user.flatMap(m -> m.getGender()).flatMap(m -> Optional.ofNullable(m.getValue()));
    }
}
