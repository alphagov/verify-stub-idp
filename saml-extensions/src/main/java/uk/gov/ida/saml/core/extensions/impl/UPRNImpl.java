package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.UPRN;

public class UPRNImpl extends StringValueSamlObjectImpl implements UPRN {

    protected UPRNImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
