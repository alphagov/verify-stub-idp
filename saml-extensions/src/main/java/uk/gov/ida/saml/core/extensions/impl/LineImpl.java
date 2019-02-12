package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.Line;

public class LineImpl extends StringValueSamlObjectImpl implements Line {

    protected LineImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }
}
