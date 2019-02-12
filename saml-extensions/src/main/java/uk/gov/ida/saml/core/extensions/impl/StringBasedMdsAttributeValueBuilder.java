package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

public class StringBasedMdsAttributeValueBuilder extends AbstractSAMLObjectBuilder<StringBasedMdsAttributeValue> {

    @Override
    public StringBasedMdsAttributeValue buildObject() {
        return buildObject(StringBasedMdsAttributeValue.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public StringBasedMdsAttributeValue buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new StringBasedMdsAttributeValueImpl(namespaceURI, localName, namespacePrefix);
    }
}
