package uk.gov.ida.saml.idp.builders;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;
import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.IpAddress;

public class IdentityProviderAuthnStatementBuilder {

    private FraudAuthnDetails fraudAuthnDetails = null;
    private AuthnContext authnContext = AuthnContext.LEVEL_1;
    private IpAddress userIpAddress = new IpAddress("9.9.8.8");

    public static IdentityProviderAuthnStatementBuilder anIdentityProviderAuthnStatement() {
        return new IdentityProviderAuthnStatementBuilder();
    }

    public IdentityProviderAuthnStatement build() {
        return (fraudAuthnDetails == null)
                ? createIdentityProviderAuthnStatement(authnContext, userIpAddress)
                : createIdentityProviderFraudAuthnStatement(fraudAuthnDetails, userIpAddress);
    }

    public IdentityProviderAuthnStatementBuilder withAuthnContext(AuthnContext authnContext) {
        this.authnContext = authnContext;
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withFraudDetails(FraudAuthnDetails fraudDetails) {
        this.fraudAuthnDetails = fraudDetails;
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withUserIpAddress(IpAddress userIpAddress) {
        this.userIpAddress = userIpAddress;
        return this;
    }
}
