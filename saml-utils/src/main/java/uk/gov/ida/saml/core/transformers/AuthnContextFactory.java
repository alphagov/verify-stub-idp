package uk.gov.ida.saml.core.transformers;

import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;

import javax.inject.Inject;

import static java.text.MessageFormat.format;
import static uk.gov.ida.saml.core.extensions.EidasAuthnContext.EIDAS_LOA_HIGH;
import static uk.gov.ida.saml.core.extensions.EidasAuthnContext.EIDAS_LOA_LOW;
import static uk.gov.ida.saml.core.extensions.EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL;

public class AuthnContextFactory {

    public static final String LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE = "Level of assurance ''{0}'' is not a recognised value.";

    @Inject
    public AuthnContextFactory() {}

    public AuthnContext mapFromEidasToLoA(String eIDASLevelOfAssurance) {
        switch (eIDASLevelOfAssurance) {
            case EIDAS_LOA_LOW:
                return AuthnContext.LEVEL_1;
            case EIDAS_LOA_SUBSTANTIAL:
            case EIDAS_LOA_HIGH:
                return AuthnContext.LEVEL_2;
            default:
                throw new IllegalStateException(format(LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE, eIDASLevelOfAssurance));
        }
    }

    public String mapFromLoAToEidas(AuthnContext levelOfAssurance) {
        switch (levelOfAssurance) {
            case LEVEL_1:
                return EIDAS_LOA_LOW;
            case LEVEL_2:
                return EIDAS_LOA_SUBSTANTIAL;
            default:
                // We currently don't support anything above Level 2.
                throw new IllegalStateException(format(LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE, levelOfAssurance.toString()));
        }
    }

    public AuthnContext authnContextForLevelOfAssurance(String levelOfAssurance) {
        switch (levelOfAssurance) {
            case IdaAuthnContext.LEVEL_1_AUTHN_CTX:
                return AuthnContext.LEVEL_1;
            case IdaAuthnContext.LEVEL_2_AUTHN_CTX:
                return AuthnContext.LEVEL_2;
            case IdaAuthnContext.LEVEL_3_AUTHN_CTX:
                return AuthnContext.LEVEL_3;
            case IdaAuthnContext.LEVEL_4_AUTHN_CTX:
                return AuthnContext.LEVEL_4;
            case IdaAuthnContext.LEVEL_X_AUTHN_CTX:
                return AuthnContext.LEVEL_X;
            default:
                throw new IllegalStateException(format(LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE, levelOfAssurance));
        }
    }
}
