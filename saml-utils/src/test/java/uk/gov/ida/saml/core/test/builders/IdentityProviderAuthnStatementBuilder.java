package uk.gov.ida.saml.core.test.builders;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.IpAddress;

import java.util.Optional;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;
import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderFraudAuthnStatement;

public class IdentityProviderAuthnStatementBuilder {

    private Optional<FraudAuthnDetails> fraudAuthnDetails = Optional.empty();
    private AuthnContext authnContext = AuthnContext.LEVEL_1;
    private Optional<IpAddress> userIpAddress = Optional.ofNullable(IpAddressBuilder.anIpAddress().build());

    public static IdentityProviderAuthnStatementBuilder anIdentityProviderAuthnStatement() {
        return new IdentityProviderAuthnStatementBuilder();
    }

    public IdentityProviderAuthnStatement build() {
        if (fraudAuthnDetails.isPresent()) {
            return createIdentityProviderFraudAuthnStatement(fraudAuthnDetails.get(), userIpAddress.orElse(null));
        }
        return createIdentityProviderAuthnStatement(authnContext, userIpAddress.orElse(null));
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
