package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.UPRN;

public class UPRNBuilder extends AbstractSAMLObjectBuilder<UPRN> {

    @Override
    public UPRN buildObject() {
        return buildObject(InternationalPostCode.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public UPRN buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new UPRNImpl(namespaceURI, localName, namespacePrefix);
    }
}
