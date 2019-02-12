package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.StatusValue;

public class StatusValueBuilder extends AbstractSAMLObjectBuilder<StatusValue> {

    @Override
    public StatusValue buildObject() {
        return buildObject(StatusValue.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public StatusValue buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new StatusValueImpl(namespaceURI, localName, namespacePrefix);
    }
}
