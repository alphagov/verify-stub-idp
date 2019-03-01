package uk.gov.ida.stub.idp.domain;

import java.net.URI;

public interface SamlResponse {
    String getResponseString();
    String getRelayState();
    URI getHubUrl();
}
