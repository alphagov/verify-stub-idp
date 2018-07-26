package uk.gov.ida.stub.idp.builders;

import uk.gov.ida.stub.idp.configuration.StubIdp;
import uk.gov.ida.stub.idp.configuration.UserCredentials;
import uk.gov.ida.stub.idp.utils.TestStubIdp;
import uk.gov.ida.stub.idp.utils.TestUserCredentials;

import java.util.ArrayList;
import java.util.List;

public class StubIdpBuilder {
    private String assetId = "default-stub-idp";
    private String displayName = "Default Stub IDP";
    private String friendlyId = "default-stub-idp";
    private List<UserCredentials> idpUserCredentials = new ArrayList<>();

    public static StubIdpBuilder aStubIdp() {
        return new StubIdpBuilder();
    }

    public StubIdp build() {
        if (idpUserCredentials.isEmpty()) {
            idpUserCredentials.add(new TestUserCredentials("foo", "bar"));
        }

        return new TestStubIdp(assetId, displayName, friendlyId, idpUserCredentials);
    }

    public StubIdpBuilder withId(String id) {
        this.assetId = id;
        this.friendlyId = id;
        return this;
    }

    public StubIdpBuilder withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public StubIdpBuilder addUserCredentials(UserCredentials credentials) {
        this.idpUserCredentials.add(credentials);
        return this;
    }
}
