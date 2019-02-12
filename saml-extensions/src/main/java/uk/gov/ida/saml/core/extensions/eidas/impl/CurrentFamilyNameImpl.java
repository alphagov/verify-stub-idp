package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;

import javax.annotation.Nullable;
import java.util.List;

public class CurrentFamilyNameImpl extends AbstractTransliterableString implements CurrentFamilyName {

    /** String to hold the family name. */
    private String familyName;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected CurrentFamilyNameImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getFamilyName() {
        return familyName;
    }

    /** {@inheritDoc} */
    public void setFamilyName(String s) {

        familyName = prepareForAssignment(familyName, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
