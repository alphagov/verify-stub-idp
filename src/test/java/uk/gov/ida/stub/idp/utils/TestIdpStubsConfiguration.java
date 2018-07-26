package uk.gov.ida.stub.idp.utils;

import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdp;

import java.util.Collection;

public class TestIdpStubsConfiguration extends IdpStubsConfiguration {
    public TestIdpStubsConfiguration(Collection<StubIdp> stubIdps) {
        this.stubIdps = stubIdps;
    }
}
