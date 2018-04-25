package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;

import java.util.Optional;

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

    public Optional<DatabaseIdpUser> getUser(String username, String password) {
        Optional<DatabaseIdpUser> userForStubCountry = allIdpsUserRepository.getUserForIdp(friendlyId, username);
        if (userForStubCountry.isPresent() && userForStubCountry.get().getPassword().equals(password)) {
            return userForStubCountry;
        }

        return Optional.empty();
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
}
