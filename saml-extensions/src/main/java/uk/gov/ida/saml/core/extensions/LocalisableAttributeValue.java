package uk.gov.ida.saml.core.extensions;


import org.opensaml.saml.common.SAMLObject;

public interface LocalisableAttributeValue extends SAMLObject {

    String LANGUAGE_ATTRIB_NAME = "Language";

    String getLanguage();

    void setLanguage(String language);
}
