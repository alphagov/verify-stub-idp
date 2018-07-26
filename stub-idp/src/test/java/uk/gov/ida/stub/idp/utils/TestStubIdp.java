package uk.gov.ida.stub.idp.utils;

import uk.gov.ida.stub.idp.configuration.StubIdp;
import uk.gov.ida.stub.idp.configuration.UserCredentials;

import java.util.List;

public class TestStubIdp extends StubIdp {

    public TestStubIdp(String assetId, String displayName, String friendlyId, List<UserCredentials> idpUserCredentials) {
        this.assetId = assetId;
        this.displayName = displayName;
        this.friendlyId = friendlyId;
        this.idpUserCredentials = idpUserCredentials;
    }

}
