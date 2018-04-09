package uk.gov.ida.stub.idp.domain;

import java.net.URI;

public class SamlResponse {
    private final String idpResponse;
    private final String relayState;
    private final URI hubUrl;

    public SamlResponse(String idpResponse, String relayState, URI hubUrl) {
        this.idpResponse = idpResponse;
        this.relayState = relayState;
        this.hubUrl = hubUrl;
    }

    public String getRelayState() {
        return relayState;
    }

    public String getIdpResponse() {
        return idpResponse;
    }

    public URI getHubUrl() {
        return hubUrl;
    }
}
