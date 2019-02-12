package uk.gov.ida.saml.core.extensions;


import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.AttributeValue;

public interface BooleanBasedMdsAttributeValue extends AttributeValue, SAMLObject {
    boolean getValue();

    void setValue(boolean value);
}
