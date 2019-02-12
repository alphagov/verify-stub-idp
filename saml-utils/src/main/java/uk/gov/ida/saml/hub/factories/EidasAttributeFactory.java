package uk.gov.ida.saml.hub.factories;

import org.joda.time.LocalDate;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import uk.gov.ida.saml.core.IdaConstants.Eidas_Attributes;
import uk.gov.ida.saml.core.IdaConstants.Eidas_Attributes.FamilyName;
import uk.gov.ida.saml.core.IdaConstants.Eidas_Attributes.FirstName;
import uk.gov.ida.saml.core.extensions.eidas.CurrentAddress;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.extensions.eidas.Gender;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentAddressBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentFamilyNameBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentGivenNameBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.GenderBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.PersonIdentifierBuilder;

import java.util.Arrays;


public class EidasAttributeFactory {

    private Attribute buildAttribute(String friendlyName, String name, AttributeValue... attributeValues) {
        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setFriendlyName(friendlyName);
        attribute.setName(name);
        attribute.setNameFormat(Attribute.URI_REFERENCE);
        attribute.getAttributeValues().addAll(Arrays.asList(attributeValues));
        return attribute;
    }

    public Attribute createFirstNameAttribute(String firstName) {
        CurrentGivenName firstNameAttributeValue = new CurrentGivenNameBuilder().buildObject();
        firstNameAttributeValue.setFirstName(firstName);
        return buildAttribute(FirstName.FRIENDLY_NAME, FirstName.NAME, firstNameAttributeValue);
    }

    public Attribute createFamilyName(String familyName) {
        CurrentFamilyName familyNameAttributeValue = new CurrentFamilyNameBuilder().buildObject();
        familyNameAttributeValue.setFamilyName(familyName);
        return buildAttribute(FamilyName.FRIENDLY_NAME, FamilyName.NAME, familyNameAttributeValue);
    }

    public Attribute createDateOfBirth(LocalDate dateOfBirth) {
        DateOfBirth dateOfBirthAttributeValue = new DateOfBirthBuilder().buildObject();
        dateOfBirthAttributeValue.setDateOfBirth(dateOfBirth);
        return buildAttribute(Eidas_Attributes.DateOfBirth.FRIENDLY_NAME, Eidas_Attributes.DateOfBirth.NAME, dateOfBirthAttributeValue);
    }

    public Attribute createPersonIdentifier(String personIdentifier) {
        PersonIdentifier personIdentifierAttributeValue = new PersonIdentifierBuilder().buildObject();
        personIdentifierAttributeValue.setPersonIdentifier(personIdentifier);
        return buildAttribute(Eidas_Attributes.PersonIdentifier.FRIENDLY_NAME, Eidas_Attributes.PersonIdentifier.NAME, personIdentifierAttributeValue);
    }

    public Attribute createCurrentAddress(String address) {
        CurrentAddress currentAddressAttributeValue = new CurrentAddressBuilder().buildObject();
        currentAddressAttributeValue.setCurrentAddress(address);
        return buildAttribute(Eidas_Attributes.CurrentAddress.FRIENDLY_NAME, Eidas_Attributes.CurrentAddress.NAME, currentAddressAttributeValue);
    }

    public Attribute createGender(String gender) {
        Gender genderAttributeValue = new GenderBuilder().buildObject();
        genderAttributeValue.setValue(gender);
        return buildAttribute(Eidas_Attributes.Gender.FRIENDLY_NAME, Eidas_Attributes.Gender.NAME, genderAttributeValue);
    }
}
