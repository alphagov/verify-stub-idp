package uk.gov.ida.saml.core.extensions.impl;


import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.Unmarshaller;
import uk.gov.ida.saml.core.extensions.PersonName;

import javax.xml.namespace.QName;

public class PersonNameImpl extends StringBasedMdsAttributeValueImpl implements PersonName {

    public static final Marshaller MARSHALLER = new LocalisableStringBasedMdsAttributeValueMarshaller(PersonName.TYPE_LOCAL_NAME);
    public static final Unmarshaller UNMARSHALLER = new LocalisableStringBasedMdsAttributeValueUnmarshaller();

    private String language;

    protected PersonNameImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        this(namespaceURI, elementLocalName, namespacePrefix, PersonName.TYPE_NAME);
    }

    protected PersonNameImpl(String namespaceURI, String elementLocalName, String namespacePrefix, QName typeName) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        super.setSchemaType(typeName);
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(String language) {
        this.language = prepareForAssignment(this.language, language);
    }
}
