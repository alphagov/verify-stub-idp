package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.CurrentAddress;

import javax.annotation.Nullable;
import java.util.List;

public class CurrentAddressImpl extends AbstractSAMLObject implements CurrentAddress {

    /** String to hold the address in base64 encoded. */
    private String currentAddressInBase64Encoded;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected CurrentAddressImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getCurrentAddress() {
        return currentAddressInBase64Encoded;
    }

    /** {@inheritDoc} */
    public void setCurrentAddress(String s) {

        currentAddressInBase64Encoded = prepareForAssignment(currentAddressInBase64Encoded, s);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
