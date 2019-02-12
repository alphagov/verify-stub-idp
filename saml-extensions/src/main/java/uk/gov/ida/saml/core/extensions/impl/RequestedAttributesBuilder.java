package uk.gov.ida.saml.core.extensions.impl;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.RequestedAttributes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RequestedAttributesBuilder extends AbstractSAMLObjectBuilder<RequestedAttributes> {

    @Nonnull
    @Override
    public RequestedAttributes buildObject() {
        return buildObject(IdaConstants.EIDAS_NS, RequestedAttributes.DEFAULT_ELEMENT_LOCAL_NAME, IdaConstants.EIDAS_PREFIX);
    }

    @Nonnull
    @Override
    public RequestedAttributes buildObject(@Nullable String namespaceURI, @Nonnull @NotEmpty String localName, @Nullable String namespacePrefix) {
        return new RequestedAttributesImpl(namespaceURI, localName, namespacePrefix);
    }
}
