package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;

public class CurrentGivenNameBuilder extends AbstractSAMLObjectBuilder<CurrentGivenName> {

    /**
     * Constructor.
     */
    public CurrentGivenNameBuilder() {

    }

    /** {@inheritDoc} */
    public CurrentGivenName buildObject() {
        return buildObject(CurrentGivenName.DEFAULT_ELEMENT_NAME, CurrentGivenName.TYPE_NAME);
    }

    /** {@inheritDoc} */
    public CurrentGivenName buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new CurrentGivenNameImpl(namespaceURI, localName, namespacePrefix);
    }
}
