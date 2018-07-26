package uk.gov.ida.apprule.support.eidas;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Status;

public class InboundResponseFromCountry {

    private String issuer;
    private Assertion validatedIdentityAssertion;
    private Status status;

    private InboundResponseFromCountry() {
    }

    public InboundResponseFromCountry(
            String issuer,
            Assertion validatedIdentityAssertion,
            Status status
    ) {
        this.issuer = issuer;
        this.validatedIdentityAssertion = validatedIdentityAssertion;
        this.status = status;
    }

    public String getIssuer() {
        return issuer;
    }

    public Assertion getValidatedIdentityAssertion() {
        return validatedIdentityAssertion;
    }

    public Status getStatus() {
        return status;
    }
}
