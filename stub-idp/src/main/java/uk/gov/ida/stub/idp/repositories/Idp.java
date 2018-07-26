package uk.gov.ida.stub.idp.repositories;

import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Idp {

    private final String friendlyId;
    private final String displayName;
    private final String assetId;
    private boolean sendKeyInfo;
    private String issuerId;
    private AllIdpsUserRepository allIdpsUserRepository;

    public Idp(String displayName, String friendlyId, String assetId, boolean sendKeyInfo, String issuerId, AllIdpsUserRepository allIdpsUserRepository) {
        this.friendlyId = friendlyId;
        this.displayName = displayName;
        this.assetId = assetId;
        this.sendKeyInfo = sendKeyInfo;
        this.issuerId = issuerId;
        this.allIdpsUserRepository = allIdpsUserRepository;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public String getAssetId() {
        return assetId;
    }

    public boolean shouldSendKeyInfo() {
        return sendKeyInfo;
    }

    public Optional<DatabaseIdpUser> getUser(String username, String password) {
        Optional<DatabaseIdpUser> userForIdp = allIdpsUserRepository.getUserForIdp(friendlyId, username);
        if (userForIdp.isPresent() && userForIdp.get().getPassword().equals(password)) {
            return userForIdp;
        }

        return Optional.empty();
    }

    public DatabaseIdpUser createUser(
            Optional<String> pid,
            List<MatchingDatasetValue<String>> firstnames,
            List<MatchingDatasetValue<String>> middleNames,
            List<MatchingDatasetValue<String>> surnames,
            Optional<MatchingDatasetValue<Gender>> gender,
            List<MatchingDatasetValue<LocalDate>> dateOfBirths,
            List<Address> addresses,
            String username,
            String password,
            AuthnContext levelOfAssurance) {

        String pidValue = pid.isPresent() ? pid.get() : UUID.randomUUID().toString();
        return allIdpsUserRepository.createUserForIdp(friendlyId, pidValue, firstnames, middleNames, surnames, gender, dateOfBirths, addresses, username, password, levelOfAssurance);
    }

    public void deleteUser(String username) {
        allIdpsUserRepository.deleteUserFromIdp(friendlyId, username);
    }

    public boolean userExists(String username) {
        return allIdpsUserRepository.containsUserForIdp(friendlyId, username);
    }

    public Optional<DatabaseIdpUser> getUser(String username) {
        return allIdpsUserRepository.getUserForIdp(friendlyId, username);
    }

    public Collection<DatabaseIdpUser> getAllUsers() {
        return allIdpsUserRepository.getAllUsersForIdp(friendlyId);
    }

    public String getIssuerId() {
        return issuerId;
    }
}
