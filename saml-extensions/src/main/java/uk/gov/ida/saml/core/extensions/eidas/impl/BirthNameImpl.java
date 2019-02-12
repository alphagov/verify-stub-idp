package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.BirthName;

import javax.annotation.Nullable;
import java.util.List;

public class BirthNameImpl extends AbstractSAMLObject implements BirthName {

    /** String to hold the birth name. */
    private String birthName;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected BirthNameImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getBirthName() {
        return birthName;
    }

    /** {@inheritDoc} */
    public void setBirthName(String s) {

        birthName = prepareForAssignment(birthName, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
