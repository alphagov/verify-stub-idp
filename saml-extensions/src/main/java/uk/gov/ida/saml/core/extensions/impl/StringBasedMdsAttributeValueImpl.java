package uk.gov.ida.saml.core.extensions.impl;

import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;

public class StringBasedMdsAttributeValueImpl extends StringValueSamlObjectImpl implements StringBasedMdsAttributeValue {

    private org.joda.time.DateTime fromTime;
    private org.joda.time.DateTime toTime;
    private boolean verified;

    protected StringBasedMdsAttributeValueImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Override
    public org.joda.time.DateTime getFrom() {
        return fromTime;
    }

    @Override
    public void setFrom(org.joda.time.DateTime fromTime) {
        this.fromTime = fromTime;
    }

    @Override
    public org.joda.time.DateTime getTo() {
        return toTime;
    }

    @Override
    public void setTo(org.joda.time.DateTime toTime) {
        this.toTime = toTime;
    }

    @Override
    public boolean getVerified() {
        return verified;
    }

    @Override
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
