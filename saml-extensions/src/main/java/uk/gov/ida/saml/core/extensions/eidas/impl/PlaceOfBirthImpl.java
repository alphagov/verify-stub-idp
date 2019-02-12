package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.PlaceOfBirth;

import javax.annotation.Nullable;
import java.util.List;

public class PlaceOfBirthImpl extends AbstractSAMLObject implements PlaceOfBirth {

    /** String to hold the place of birth. */
    private String placeOfBirth;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PlaceOfBirthImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    /** {@inheritDoc} */
    public void setPlaceOfBirth(String s) {

        placeOfBirth = prepareForAssignment(placeOfBirth, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
