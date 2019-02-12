package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;

public class PersonIdentifierBuilder extends AbstractSAMLObjectBuilder<PersonIdentifier> {

    /**
     * Constructor.
     */
    public PersonIdentifierBuilder() {

    }

    /** {@inheritDoc} */
    public PersonIdentifier buildObject() {
        return buildObject(PersonIdentifier.DEFAULT_ELEMENT_NAME, PersonIdentifier.TYPE_NAME);
    }

    /** {@inheritDoc} */
    public PersonIdentifier buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new PersonIdentifierImpl(namespaceURI, localName, namespacePrefix);
    }
}
