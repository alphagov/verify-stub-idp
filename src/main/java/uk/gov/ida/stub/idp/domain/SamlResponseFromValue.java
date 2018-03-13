package uk.gov.ida.stub.idp.domain;

import java.net.URI;
import java.util.function.Function;

public class SamlResponseFromValue<T> implements SamlResponse {
    private final T response;
    private final Function<T, String> transformer;
    private final String relayState;
    private final URI hubUrl;

    public SamlResponseFromValue(T response, Function<T, String> transformer, String relayState, URI hubUrl) {
        this.response = response;
        this.transformer = transformer;
        this.relayState = relayState;
        this.hubUrl = hubUrl;
    }

    public T getValue() {
        return response;
    }

    public String getRelayState() {
        return relayState;
    }

    public String getResponse() {
        return transformer.apply(response);
    }

    public URI getHubUrl() {
        return hubUrl;
    }
}
