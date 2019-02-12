package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

import java.net.URI;

public abstract class IdaSamlMessage extends IdaMessage {

    private URI destination;

    protected IdaSamlMessage() {
    }

    public IdaSamlMessage(String id, String issuer, DateTime issueInstant, URI destination) {
        super(id, issuer, issueInstant);
        this.destination = destination;
    }

    public URI getDestination() {
        return destination;
    }
}
