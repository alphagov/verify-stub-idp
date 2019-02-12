package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.Gpg45Status;

public class Gpg45StatusBuilder extends AbstractSAMLObjectBuilder<Gpg45Status> {

    @Override
    public Gpg45Status buildObject() {
        return buildObject(Gpg45Status.DEFAULT_ELEMENT_NAME);
    }

    @Override
    public Gpg45Status buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new Gpg45StatusImpl(namespaceURI, localName, namespacePrefix);
    }
}
