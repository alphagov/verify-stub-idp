package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.saml.common.AbstractSAMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.RequestedAttributes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RequestedAttributesImpl extends AbstractSAMLObject implements RequestedAttributes {

    public static final Marshaller MARSHALLER = new AbstractSAMLObjectMarshaller() { };
    public static final Unmarshaller UNMARSHALLER = new RequestedAttributesUnmarshaller();

    private List<XMLObject> requestedAttributeObjects = new ArrayList<>();

    RequestedAttributesImpl(@Nullable String namespaceURI, @Nonnull String elementLocalName, @Nullable String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return Collections.unmodifiableList(requestedAttributeObjects);
    }

    public void setRequestedAttributes(RequestedAttribute... requestedAttribute) {
        this.requestedAttributeObjects = Arrays.asList(requestedAttribute);
    }

    @Override
    public void addRequestedAttribute(RequestedAttribute requestedAttribute) {
        requestedAttributeObjects.add(requestedAttribute);
    }
}
