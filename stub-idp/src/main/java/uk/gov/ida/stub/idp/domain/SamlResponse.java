package uk.gov.ida.stub.idp.domain;

import java.net.URI;

public interface SamlResponse {
    public String getResponseString();
    public String getRelayState();
    public URI getHubUrl();
}
