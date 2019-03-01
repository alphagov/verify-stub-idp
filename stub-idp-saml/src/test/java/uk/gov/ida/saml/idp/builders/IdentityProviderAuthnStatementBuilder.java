package uk.gov.ida.saml.idp.builders;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;
import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

import java.util.Optional;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.IpAddress;

public class IdentityProviderAuthnStatementBuilder {

    private Optional<FraudAuthnDetails> fraudAuthnDetails = Optional.empty();
    private AuthnContext authnContext = AuthnContext.LEVEL_1;
    private Optional<IpAddress> userIpAddress = Optional.of(new IpAddress("9.9.8.8"));

    public static IdentityProviderAuthnStatementBuilder anIdentityProviderAuthnStatement() {
        return new IdentityProviderAuthnStatementBuilder();
    }

    public IdentityProviderAuthnStatement build() {
        return fraudAuthnDetails.map(fraudAuthnDetails1 -> createIdentityProviderFraudAuthnStatement(fraudAuthnDetails1, userIpAddress.orElse(null))).orElseGet(() -> createIdentityProviderAuthnStatement(authnContext, userIpAddress.orElse(null)));
    }

    public IdentityProviderAuthnStatementBuilder withAuthnContext(AuthnContext authnContext) {
        this.authnContext = authnContext;
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withFraudDetails(FraudAuthnDetails fraudDetails) {
        this.fraudAuthnDetails = Optional.ofNullable(fraudDetails);
        return this;
    }

    public IdentityProviderAuthnStatementBuilder withUserIpAddress(IpAddress userIpAddress) {
        this.userIpAddress = Optional.ofNullable(userIpAddress);
        return this;
    }
}
