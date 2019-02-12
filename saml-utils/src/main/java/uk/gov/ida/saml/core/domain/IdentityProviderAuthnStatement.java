package uk.gov.ida.saml.core.domain;

import java.util.Optional;

public final class IdentityProviderAuthnStatement {

    private Optional<FraudAuthnDetails> fraudAuthnDetails;
    private IpAddress userIpAddress;
    private AuthnContext levelOfAssurance;

    private IdentityProviderAuthnStatement(
            AuthnContext levelOfAssurance,
            Optional<FraudAuthnDetails> fraudAuthnDetails,
            IpAddress userIpAddress) {

        this.levelOfAssurance = levelOfAssurance;
        this.fraudAuthnDetails = fraudAuthnDetails;
        this.userIpAddress = userIpAddress;
    }

    public AuthnContext getAuthnContext() {
        return levelOfAssurance;
    }

    public static IdentityProviderAuthnStatement createIdentityProviderAuthnStatement(
            AuthnContext levelOfAssurance,
            IpAddress userIpAddress) {

        return new IdentityProviderAuthnStatement(levelOfAssurance, Optional.empty(), userIpAddress);
    }

    public static IdentityProviderAuthnStatement createIdentityProviderFraudAuthnStatement(
            FraudAuthnDetails fraudAuthnDetails,
            IpAddress userIpAddress) {

        return new IdentityProviderAuthnStatement(AuthnContext.LEVEL_X, Optional.ofNullable(fraudAuthnDetails), userIpAddress);
    }

    public boolean isFraudAuthnStatement() {
        return fraudAuthnDetails.isPresent();
    }

    public FraudAuthnDetails getFraudAuthnDetails() {
        return fraudAuthnDetails.orElse(null);
    }

    public IpAddress getUserIpAddress() {
        return userIpAddress;
    }
}
