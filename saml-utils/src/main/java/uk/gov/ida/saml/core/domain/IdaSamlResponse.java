package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

import java.net.URI;

public abstract class IdaSamlResponse extends IdaSamlMessage implements IdaResponse {

    private String inResponseTo;

    protected IdaSamlResponse() {
    }

    protected IdaSamlResponse(
            String responseId,
            DateTime issueInstant,
            String inResponseTo,
            String issuer,
            URI destination) {

        super(responseId, issuer, issueInstant, destination);

        this.inResponseTo = inResponseTo;
    }

    public String getInResponseTo() {
        return inResponseTo;
    }
}
