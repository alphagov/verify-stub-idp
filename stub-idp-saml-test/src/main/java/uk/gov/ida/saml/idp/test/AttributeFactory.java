package uk.gov.ida.saml.idp.test;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.impl.AttributeBuilder;
import uk.gov.ida.saml.core.IdaConstants.Eidas_Attributes;
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

import static uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthImpl.DATE_OF_BIRTH_FORMAT;

public class AttributeFactory {

    private static Attribute buildAttribute(String friendlyName, String name, AttributeValue... attributeValues) {
        Attribute attribute = new AttributeBuilder().buildObject();
        attribute.setFriendlyName(friendlyName);
        attribute.setName(name);
        attribute.setNameFormat(Attribute.URI_REFERENCE);
        attribute.getAttributeValues().addAll(Arrays.asList(attributeValues));
        return attribute;
    }

    public static Attribute genderAttribute(String gender) {
        Gender genderAttributeValue = new GenderBuilder().buildObject();
        genderAttributeValue.setValue(gender);
        return buildAttribute(Eidas_Attributes.Gender.FRIENDLY_NAME, Eidas_Attributes.Gender.NAME, genderAttributeValue);
    }

    public static Attribute currentAddressAttribute(String address) {
        CurrentAddress currentAddressAttributeValue = new CurrentAddressBuilder().buildObject();
        currentAddressAttributeValue.setCurrentAddress("PGVpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz5DdXJyZW50IEFkZHJlc3M8L2VpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz4K");
        return buildAttribute(Eidas_Attributes.CurrentAddress.FRIENDLY_NAME, Eidas_Attributes.CurrentAddress.NAME, currentAddressAttributeValue);
    }

    public static Attribute firstNameAttribute(String firstName) {
        CurrentGivenName firstNameAttributeValue = new CurrentGivenNameBuilder().buildObject();
        firstNameAttributeValue.setFirstName("Javier");
        return buildAttribute(Eidas_Attributes.FirstName.FRIENDLY_NAME, Eidas_Attributes.FirstName.NAME, firstNameAttributeValue);
    }

    public static Attribute familyNameAttribute(String familyName) {
        CurrentFamilyName familyNameAttributeValue = new CurrentFamilyNameBuilder().buildObject();
        familyNameAttributeValue.setFamilyName("Garcia");
        return buildAttribute(Eidas_Attributes.FamilyName.FRIENDLY_NAME, Eidas_Attributes.FamilyName.NAME, familyNameAttributeValue);
    }

    public static Attribute dateOfBirthAttribute(String dateOfBirth) {
        DateOfBirth dateOfBirthAttributeValue = new DateOfBirthBuilder().buildObject();
        dateOfBirthAttributeValue.setDateOfBirth(DATE_OF_BIRTH_FORMAT.parseLocalDate("1965-01-01"));
        return buildAttribute(Eidas_Attributes.DateOfBirth.FRIENDLY_NAME, Eidas_Attributes.DateOfBirth.NAME, dateOfBirthAttributeValue);
    }

    public static Attribute personIdentifierAttribute(String personIdentifier) {
        PersonIdentifier personIdentifierAttributeValue = new PersonIdentifierBuilder().buildObject();
        personIdentifierAttributeValue.setPersonIdentifier("UK/GB/12345");
        return buildAttribute(Eidas_Attributes.PersonIdentifier.FRIENDLY_NAME, Eidas_Attributes.PersonIdentifier.NAME, personIdentifierAttributeValue);
    }
}
