package uk.gov.ida.saml.hub.factories;

import org.joda.time.LocalDate;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PersonName;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class AttributeFactory_1_1 implements AttributeFactory {

    private final OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    @Inject
    public AttributeFactory_1_1(OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    @Override
    public Attribute createFirstnameAttribute(List<SimpleMdsValue<String>> firstnames) {
        return createPersonNameAttribute(firstnames, IdaConstants.Attributes_1_1.Firstname.NAME, IdaConstants.Attributes_1_1.Firstname.FRIENDLY_NAME);
    }

    @Override
    public Attribute createMiddlenamesAttribute(List<SimpleMdsValue<String>> middlenames) {
        return createPersonNameAttribute(middlenames, IdaConstants.Attributes_1_1.Middlename.NAME, IdaConstants.Attributes_1_1.Middlename.FRIENDLY_NAME);
    }

    @Override
    public Attribute createSurnameAttribute(List<SimpleMdsValue<String>> surnames) {
        return createPersonNameAttribute(surnames, IdaConstants.Attributes_1_1.Surname.NAME, IdaConstants.Attributes_1_1.Surname.FRIENDLY_NAME);
    }

    @Override
    public Attribute createGenderAttribute(SimpleMdsValue<Gender> gender) {
        final uk.gov.ida.saml.core.extensions.Gender genderValue = openSamlXmlObjectFactory.createGenderAttributeValue(gender.getValue().getValue());
        genderValue.setFrom(gender.getFrom());
        genderValue.setTo(gender.getTo());
        genderValue.setVerified(gender.isVerified());
        return createAttribute(
                IdaConstants.Attributes_1_1.Gender.NAME,
                IdaConstants.Attributes_1_1.Gender.FRIENDLY_NAME,
                asList((AttributeValue) genderValue));
    }

    @Override
    public Attribute createDateOfBirthAttribute(List<SimpleMdsValue<LocalDate>> dateOfBirths) {
        return createAttribute(
                IdaConstants.Attributes_1_1.DateOfBirth.NAME,
                IdaConstants.Attributes_1_1.DateOfBirth.FRIENDLY_NAME,
                createAttributeValuesForDate(dateOfBirths)
        );
    }

    @Override
    public Attribute createCurrentAddressesAttribute(List<Address> currentAddresses) {
        Attribute currentAddressesAttribute = openSamlXmlObjectFactory.createAttribute();
        currentAddressesAttribute.setName(IdaConstants.Attributes_1_1.CurrentAddress.NAME);
        currentAddressesAttribute.setFriendlyName(IdaConstants.Attributes_1_1.CurrentAddress.FRIENDLY_NAME);
        currentAddressesAttribute.setNameFormat(Attribute.UNSPECIFIED);
        for (Address address : currentAddresses) {
            AttributeValue addressAttributeValue = createAddressAttributeValue(address);
            currentAddressesAttribute.getAttributeValues().add(addressAttributeValue);
        }
        return  currentAddressesAttribute;
    }

    @Override
    public Attribute createPreviousAddressesAttribute(List<Address> addresses) {
        Attribute addressAttribute = openSamlXmlObjectFactory.createAttribute();
        addressAttribute.setName(IdaConstants.Attributes_1_1.PreviousAddress.NAME);
        addressAttribute.setFriendlyName(IdaConstants.Attributes_1_1.PreviousAddress.FRIENDLY_NAME);
        addressAttribute.setNameFormat(Attribute.UNSPECIFIED);

        for (Address address : addresses) {
            AttributeValue addressAttributeValue = createAddressAttributeValue(address);
            addressAttribute.getAttributeValues().add(addressAttributeValue);
        }

        return  addressAttribute;
    }

    @Override
    public Attribute createCycle3DataAttribute(String attributeName, String cycle3Data) {
        AttributeValue stringBasedMdsAttributeValue = openSamlXmlObjectFactory.createSimpleMdsAttributeValue(cycle3Data);
        return createAttribute(
                attributeName,
                null,
                asList(stringBasedMdsAttributeValue)
        );
    }

    @Override
    public Attribute createIdpFraudEventIdAttribute(String fraudEventId) {
        final AttributeValue idpFraudEventId = openSamlXmlObjectFactory.createIdpFraudEventAttributeValue(fraudEventId);

        return createAttribute(
            IdaConstants.Attributes_1_1.IdpFraudEventId.NAME,
            IdaConstants.Attributes_1_1.IdpFraudEventId.FRIENDLY_NAME,
            asList(idpFraudEventId)
        );
    }

    @Override
    public Attribute createGpg45StatusAttribute(String indicator) {
        final AttributeValue gpg45StatusAttributeValue = openSamlXmlObjectFactory.createGpg45StatusAttributeValue(indicator);

        return createAttribute(
            IdaConstants.Attributes_1_1.GPG45Status.NAME,
            IdaConstants.Attributes_1_1.GPG45Status.FRIENDLY_NAME,
            asList(gpg45StatusAttributeValue)
        );
    }

    @Override
    public Attribute createUserIpAddressAttribute(String userIpAddressString) {
        final AttributeValue ipAddress = openSamlXmlObjectFactory.createIPAddressAttributeValue(userIpAddressString);

        return createAttribute(
                IdaConstants.Attributes_1_1.IPAddress.NAME,
                IdaConstants.Attributes_1_1.IPAddress.FRIENDLY_NAME,
                asList(ipAddress)
        );
    }

    private Attribute createPersonNameAttribute(final List<SimpleMdsValue<String>> names, final String attributeName, final String attributeFriendlyName) {
        List<AttributeValue> personNameAttributeValues = createAttributeValuesForPersonName(names);
        return createAttribute(
                attributeName,
                attributeFriendlyName,
                personNameAttributeValues
        );
    }

    private AttributeValue createAddressAttributeValue(Address address) {

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = openSamlXmlObjectFactory.createAddressAttributeValue();
        addressAttributeValue.setFrom(address.getFrom());
        if (address.getTo().isPresent()) {
            addressAttributeValue.setTo(address.getTo().get());
        }
        for (String lineValue : address.getLines()) {
            Line line = openSamlXmlObjectFactory.createLine(lineValue);
            addressAttributeValue.getLines().add(line);
        }
        if (address.getPostCode().isPresent()) {
            addressAttributeValue.setPostCode(openSamlXmlObjectFactory.createPostCode(address.getPostCode().get()));
        }
        if (address.getInternationalPostCode().isPresent()) {
            addressAttributeValue.setInternationalPostCode(openSamlXmlObjectFactory.createInternationalPostCode(address.getInternationalPostCode().get()));
        }
        if (address.getUPRN().isPresent()) {
            addressAttributeValue.setUPRN(openSamlXmlObjectFactory.createUPRN(address.getUPRN().get()));
        }
        addressAttributeValue.setVerified(address.isVerified());

        return addressAttributeValue;
    }

    private Attribute createAttribute(
            String attributeName,
            String attributeFriendlyName,
            List<? extends XMLObject> attributeValues) {
        Attribute nameAttribute = openSamlXmlObjectFactory.createAttribute();

        nameAttribute.setName(attributeName);
        nameAttribute.setFriendlyName(attributeFriendlyName);
        nameAttribute.setNameFormat(Attribute.UNSPECIFIED);
        nameAttribute.getAttributeValues().addAll(attributeValues);

        return nameAttribute;
    }

    private List<AttributeValue> createAttributeValuesForPersonName(List<SimpleMdsValue<String>> nameValues) {
        List<AttributeValue> personNameAttributeValues = new ArrayList<>();
        for (SimpleMdsValue<String> value : nameValues) {
            final PersonName personNameAttributeValue = openSamlXmlObjectFactory.createPersonNameAttributeValue(value.getValue());
            personNameAttributeValue.setFrom(value.getFrom());
            personNameAttributeValue.setTo(value.getTo());
            personNameAttributeValue.setVerified(value.isVerified());
            personNameAttributeValues.add(personNameAttributeValue);
        }
        return personNameAttributeValues;
    }

    private List<AttributeValue> createAttributeValuesForDate(List<SimpleMdsValue<LocalDate>> dateValues) {
        List<AttributeValue> personNameAttributeValues = new ArrayList<>();
        for (SimpleMdsValue<LocalDate> value : dateValues) {
            final Date personNameAttributeValue = openSamlXmlObjectFactory.createDateAttributeValue(value.getValue().toString("yyyy-MM-dd"));
            personNameAttributeValue.setFrom(value.getFrom());
            personNameAttributeValue.setTo(value.getTo());
            personNameAttributeValue.setVerified(value.isVerified());
            personNameAttributeValues.add(personNameAttributeValue);
        }
        return personNameAttributeValues;
    }
}
