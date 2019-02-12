package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;

import java.util.ArrayList;
import java.util.List;

public class AttributeAuthorityDescriptorBuilder {

    private List<KeyDescriptor> keyDescriptors = new ArrayList<>();

    public static AttributeAuthorityDescriptorBuilder anAttributeAuthorityDescriptor() {
        return new AttributeAuthorityDescriptorBuilder();
    }

    public AttributeAuthorityDescriptorBuilder addKeyDescriptor(KeyDescriptor keyDescriptor) {
        this.keyDescriptors.add(keyDescriptor);
        return this;
    }

    public AttributeAuthorityDescriptor build() {
        AttributeAuthorityDescriptor attributeAuthorityDescriptor = new org.opensaml.saml.saml2.metadata.impl.AttributeAuthorityDescriptorBuilder().buildObject();
        attributeAuthorityDescriptor.getKeyDescriptors().addAll(keyDescriptors);
        attributeAuthorityDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        return attributeAuthorityDescriptor;
    }
}
