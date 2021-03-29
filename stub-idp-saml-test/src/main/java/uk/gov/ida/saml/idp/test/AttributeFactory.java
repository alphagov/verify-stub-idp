package uk.gov.ida.saml.idp.test;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;

import java.util.Arrays;

public class AttributeFactory {

    private static Attribute buildAttribute(String friendlyName, String name, AttributeValue... attributeValues) {
        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setFriendlyName(friendlyName);
        attribute.setName(name);
        attribute.setNameFormat(Attribute.URI_REFERENCE);
        attribute.getAttributeValues().addAll(Arrays.asList(attributeValues));
        return attribute;
    }

}
