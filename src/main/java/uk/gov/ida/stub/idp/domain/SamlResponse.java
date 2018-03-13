package uk.gov.ida.stub.idp.domain;

import java.net.URI;

public interface SamlResponse {
    public String getResponse();
    public String getRelayState();
    public URI getHubUrl();
}
