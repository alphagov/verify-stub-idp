package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.Gender;

public class GenderBuilder extends AbstractSAMLObjectBuilder<Gender> {

    @Override
    public Gender buildObject() {
        return buildObject(Gender.DEFAULT_ELEMENT_NAME, Gender.TYPE_NAME);
    }

    @Override
    public Gender buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new GenderImpl(namespaceURI, localName, namespacePrefix, Gender.TYPE_NAME);
    }
}
