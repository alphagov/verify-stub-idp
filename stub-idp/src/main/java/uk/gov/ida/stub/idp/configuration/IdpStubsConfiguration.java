package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

public class IdpStubsConfiguration{

    @Valid
    @JsonProperty
    protected Collection<StubIdp> stubIdps = null;

    public Collection<StubIdp> getStubIdps() {
        return stubIdps.stream()
                .filter(stub -> !stub.isEidasEnabled())
                .collect(Collectors.toList());
    }

    public Collection<StubIdp> getStubCountries() {
        return stubIdps.stream()
                .filter(StubIdp::isEidasEnabled)
                .collect(Collectors.toList());
    }
}
