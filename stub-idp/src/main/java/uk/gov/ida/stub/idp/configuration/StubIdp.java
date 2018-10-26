package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class StubIdp {
    protected StubIdp() {
    }

    @NotNull
    @Valid
    @JsonProperty
    protected List<UserCredentials> idpUserCredentials;

    @NotNull
    @Valid
    @JsonProperty
    protected String friendlyId;

    @NotNull
    @Valid
    @JsonProperty
    protected String assetId;

    @NotNull
    @Valid
    @JsonProperty
    protected String displayName;

    @Valid
    @JsonProperty
    protected boolean sendKeyInfo;

    @Valid
    @JsonProperty
    protected boolean eidasEnabled;


    public List<UserCredentials> getIdpUserCredentials() {
        return idpUserCredentials;
    }

    public String getFriendlyId() {
        return friendlyId;
    }

    public String getAssetId() { return assetId; }

    public String getDisplayName() { return displayName; }

    public boolean getSendKeyInfo() {
        return sendKeyInfo;
    }

    public boolean isEidasEnabled() {
        return eidasEnabled;
    }
}
