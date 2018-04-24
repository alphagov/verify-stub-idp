package uk.gov.ida.stub.idp.repositories;

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
