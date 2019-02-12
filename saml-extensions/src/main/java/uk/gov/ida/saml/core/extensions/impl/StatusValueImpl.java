package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.StatusValue;

public class StatusValueImpl extends StringValueSamlObjectImpl implements StatusValue {

    protected StatusValueImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
