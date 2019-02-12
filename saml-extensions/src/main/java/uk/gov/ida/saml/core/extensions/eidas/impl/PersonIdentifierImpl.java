package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;

import javax.annotation.Nullable;
import java.util.List;

public class PersonIdentifierImpl extends AbstractSAMLObject implements PersonIdentifier {

    /** String to hold the person identifier. */
    private String personIdentifier;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PersonIdentifierImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getPersonIdentifier() {
        return personIdentifier;
    }

    /** {@inheritDoc} */
    public void setPersonIdentifier(String s) {

        personIdentifier = prepareForAssignment(personIdentifier, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
