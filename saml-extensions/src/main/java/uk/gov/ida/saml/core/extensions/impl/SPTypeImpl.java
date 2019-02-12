package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.SPType;

public class SPTypeImpl extends StringValueSamlObjectImpl implements SPType {

    protected SPTypeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
