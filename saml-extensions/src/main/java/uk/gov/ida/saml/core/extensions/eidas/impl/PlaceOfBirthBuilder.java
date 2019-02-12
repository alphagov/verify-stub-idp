package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.eidas.PlaceOfBirth;

public class PlaceOfBirthBuilder extends AbstractSAMLObjectBuilder<PlaceOfBirth> {

    /**
     * Constructor.
     */
    public PlaceOfBirthBuilder() {

    }

    /** {@inheritDoc} */
    public PlaceOfBirth buildObject() {
        return buildObject(PlaceOfBirth.DEFAULT_ELEMENT_NAME, PlaceOfBirth.TYPE_NAME);
    }

    /** {@inheritDoc} */
    public PlaceOfBirth buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new PlaceOfBirthImpl(namespaceURI, localName, namespacePrefix);
    }
}
