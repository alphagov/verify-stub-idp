package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.eidas.CurrentAddress;

public class CurrentAddressBuilder extends AbstractSAMLObjectBuilder<CurrentAddress> {

    /**
     * Constructor.
     */
    public CurrentAddressBuilder() {

    }

    /** {@inheritDoc} */
    public CurrentAddress buildObject() {
        return buildObject(CurrentAddress.DEFAULT_ELEMENT_NAME, CurrentAddress.TYPE_NAME);
    }

    /** {@inheritDoc} */
    public CurrentAddress buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new CurrentAddressImpl(namespaceURI, localName, namespacePrefix);
    }
}
