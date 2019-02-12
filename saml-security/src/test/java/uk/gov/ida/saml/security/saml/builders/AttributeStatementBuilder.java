package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class AttributeStatementBuilder {

    private static TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    private List<Attribute> attributes = new ArrayList<>();

    public static AttributeStatementBuilder anAttributeStatement() {
        return new AttributeStatementBuilder();
    }

    public AttributeStatement build() {
        AttributeStatement attributeStatement = testSamlObjectFactory.createAttributeStatement();
        attributeStatement.getAttributes().addAll(attributes);
        return attributeStatement;
    }

    public AttributeStatementBuilder addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        return this;
    }
}
