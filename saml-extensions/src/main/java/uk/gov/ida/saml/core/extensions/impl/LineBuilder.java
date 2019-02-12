package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.Line;

public class LineBuilder extends AbstractSAMLObjectBuilder<Line> {

    @Override
    public Line buildObject() {
        return buildObject(Line.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public Line buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new LineImpl(namespaceURI, localName, namespacePrefix);
    }
}
