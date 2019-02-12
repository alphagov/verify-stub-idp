package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.eidas.Gender;

public class GenderBuilder extends AbstractSAMLObjectBuilder<Gender> {

    /**
     * Constructor.
     */
    public GenderBuilder() {

    }

    /** {@inheritDoc} */
    public Gender buildObject() {
        return buildObject(Gender.DEFAULT_ELEMENT_NAME, Gender.TYPE_NAME);
    }

    /** {@inheritDoc} */
    public Gender buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new GenderImpl(namespaceURI, localName, namespacePrefix);
    }
}
