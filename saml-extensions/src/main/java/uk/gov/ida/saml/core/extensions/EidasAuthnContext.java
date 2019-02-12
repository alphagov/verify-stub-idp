package uk.gov.ida.saml.core.extensions;

import org.opensaml.saml.saml2.core.AuthnContext;

public interface EidasAuthnContext extends AuthnContext {
    String EIDAS_LOA_LOW = "http://eidas.europa.eu/LoA/low";

    String EIDAS_LOA_HIGH = "http://eidas.europa.eu/LoA/high";

    String EIDAS_LOA_SUBSTANTIAL = "http://eidas.europa.eu/LoA/substantial";
}
