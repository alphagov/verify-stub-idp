package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.util.Collection;

public class IdpStubsConfiguration{

    @Valid
    @JsonProperty
    protected Collection<StubIdp> stubIdps = null;

    public Collection<StubIdp> getStubIdps() {
        return stubIdps;
    }
}
