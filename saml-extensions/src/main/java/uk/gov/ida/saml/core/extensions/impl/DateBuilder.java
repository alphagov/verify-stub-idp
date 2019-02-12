package uk.gov.ida.saml.core.extensions.impl;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;
import uk.gov.ida.saml.core.extensions.Date;

public class DateBuilder extends AbstractSAMLObjectBuilder<Date> {
    @Override
    public Date buildObject() {
        return buildObject(Date.DEFAULT_ELEMENT_NAME, Date.TYPE_NAME);
    }

    @Override
    public Date buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new DateImpl(namespaceURI, localName, namespacePrefix, Date.TYPE_NAME);
    }
}
