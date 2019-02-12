package uk.gov.ida.saml.core.extensions;

import org.opensaml.saml.common.SAMLObject;

public interface StringValueSamlObject extends SAMLObject {
    String getValue();

    void setValue(String value);
}
