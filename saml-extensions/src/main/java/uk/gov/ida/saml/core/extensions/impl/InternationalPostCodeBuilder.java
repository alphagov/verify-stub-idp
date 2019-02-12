package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;

public class InternationalPostCodeBuilder extends AbstractSAMLObjectBuilder<InternationalPostCode> {

    @Override
    public InternationalPostCode buildObject() {
        return buildObject(InternationalPostCode.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public InternationalPostCode buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new InternationalPostCodeImpl(namespaceURI, localName, namespacePrefix);
    }
}
