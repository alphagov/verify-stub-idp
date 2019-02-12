package uk.gov.ida.saml.core.extensions;


import org.opensaml.saml.saml2.core.AuthnContext;

public interface IdaAuthnContext extends AuthnContext {
    String LEVEL_1_AUTHN_CTX = "urn:uk:gov:cabinet-office:tc:saml:authn-context:level1";

    String LEVEL_2_AUTHN_CTX = "urn:uk:gov:cabinet-office:tc:saml:authn-context:level2";

    String LEVEL_3_AUTHN_CTX = "urn:uk:gov:cabinet-office:tc:saml:authn-context:level3";

    String LEVEL_4_AUTHN_CTX = "urn:uk:gov:cabinet-office:tc:saml:authn-context:level4";

    String LEVEL_X_AUTHN_CTX = "urn:uk:gov:cabinet-office:tc:saml:authn-context:levelX";
}
