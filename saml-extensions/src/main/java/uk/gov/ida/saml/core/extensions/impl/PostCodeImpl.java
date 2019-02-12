package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.PostCode;

public class PostCodeImpl extends StringValueSamlObjectImpl implements PostCode {

    protected PostCodeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
