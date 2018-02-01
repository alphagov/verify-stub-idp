package uk.gov.ida.stub.idp.domain;

import java.net.URI;

public class SamlResponse {
    private final String response;
    private final String relayState;
    private final URI hubUrl;

    public SamlResponse(String response, String relayState, URI hubUrl) {
        this.response = response;
        this.relayState = relayState;
        this.hubUrl = hubUrl;
    }

    public String getRelayState() {
        return relayState;
    }

    public String getResponse() {
        return response;
    }

    public URI getHubUrl() {
        return hubUrl;
    }
}
