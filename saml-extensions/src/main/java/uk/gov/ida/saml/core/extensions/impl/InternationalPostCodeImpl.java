package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.InternationalPostCode;

public class InternationalPostCodeImpl extends StringValueSamlObjectImpl implements InternationalPostCode {

    protected InternationalPostCodeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
