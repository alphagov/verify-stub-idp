package uk.gov.ida.stub.idp.repositories;

import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.Optional;
import java.util.UUID;

public class StubCountry {

    private final String friendlyId;
    private final String displayName;
    private final String assetId;
    private String issuerId;
    private AllIdpsUserRepository allIdpsUserRepository;

    public StubCountry(String friendlyId, String displayName, String assetId, String issuerId, AllIdpsUserRepository allIdpsUserRepository) {
        this.friendlyId = friendlyId;
        this.displayName = displayName;
        this.assetId = assetId;
        this.issuerId = issuerId;
        this.allIdpsUserRepository = allIdpsUserRepository;
    }

    public Optional<DatabaseEidasUser> getUser(String username, String password) {
        Optional<DatabaseEidasUser> userForStubCountry = allIdpsUserRepository.getUserForCountry(friendlyId, username);
        if (userForStubCountry.isPresent() && userForStubCountry.get().getPassword().equals(password)) {
            return userForStubCountry;
        }

        return Optional.empty();
    }

    public DatabaseEidasUser createUser(String username, String password, MatchingDatasetValue<String> firstName, MatchingDatasetValue<String> surname, MatchingDatasetValue<LocalDate> dateOfBirth, AuthnContext levelOfAssurance){
        return allIdpsUserRepository.createUserForStubCountry(friendlyId, UUID.randomUUID().toString(), username, password, firstName, surname, dateOfBirth, levelOfAssurance);
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getIssuerId() {
        return issuerId;
    }

    public boolean userExists(String username) {
        return allIdpsUserRepository.containsUserForIdp(friendlyId, username);
    }
}
