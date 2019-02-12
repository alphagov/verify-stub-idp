package uk.gov.ida.saml.core.test.builders;

import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

public class PersonIdentifierAttributeBuilder {
    private PersonIdentifier pid = null;
    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    public static PersonIdentifierAttributeBuilder aPersonIdentifier() {
        return new PersonIdentifierAttributeBuilder();
    }

    public PersonIdentifierAttributeBuilder withValue(PersonIdentifier personIdentifier) {
        this.pid = personIdentifier;
        return this;
    }

    public Attribute build() {
        Attribute attribute = openSamlXmlObjectFactory.createAttribute();

        attribute.getAttributeValues().add(pid);

        attribute.setName(IdaConstants.Eidas_Attributes.PersonIdentifier.NAME);
        attribute.setFriendlyName(IdaConstants.Eidas_Attributes.PersonIdentifier.FRIENDLY_NAME);
        attribute.setNameFormat(Attribute.UNSPECIFIED);

        return attribute;
    }
}
