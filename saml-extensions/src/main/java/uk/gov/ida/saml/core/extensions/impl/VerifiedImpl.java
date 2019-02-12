package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.Verified;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class VerifiedImpl extends AbstractSAMLObject implements Verified {
    public static final Marshaller MARSHALLER = new BooleanBasedMdsAttributeValueMarshaller(Verified.TYPE_LOCAL_NAME);
    public static final Unmarshaller UNMARSHALLER = new BooleanBasedMdsAttributeValueUnmarshaller();

    private boolean value;

    protected VerifiedImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        this(namespaceURI, elementLocalName, namespacePrefix, Verified.TYPE_NAME);
    }

    protected VerifiedImpl(String namespaceURI, String elementLocalName, String namespacePrefix, QName typeName) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        super.setSchemaType(typeName);
    }

    @Override
    public boolean getValue() {
        return value;
    }

    @Override
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        return new ArrayList<>();
    }
}
