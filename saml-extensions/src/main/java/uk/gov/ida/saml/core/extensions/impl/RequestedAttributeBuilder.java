package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;

import javax.annotation.Nonnull;

public class RequestedAttributeBuilder extends AbstractSAMLObjectBuilder<RequestedAttribute> {

    @Nonnull
    @Override
    public RequestedAttribute buildObject() {
        return buildObject(IdaConstants.EIDAS_NS, RequestedAttribute.DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);
    }

    @Nonnull
    @Override
    public RequestedAttribute buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new RequestedAttributeImpl(namespaceURI, localName, namespacePrefix);
    }
}

