package uk.gov.ida.saml.hub.factories;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.saml.core.extensions.Gpg45Status;
import uk.gov.ida.saml.core.extensions.IPAddress;
import uk.gov.ida.saml.core.extensions.IdpFraudEventId;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.builders.AddressBuilder;
import uk.gov.ida.saml.core.test.builders.SimpleMdsValueBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.jodatime.api.Assertions.assertThat;

@RunWith(OpenSAMLMockitoRunner.class)
public class AttributeFactory_1_1Test {

    private AttributeFactory_1_1 attributeFactory;

    @Before
    public void setup() {
        attributeFactory = new AttributeFactory_1_1(new OpenSamlXmlObjectFactory());
    }

    @Test
    public void createFirstNameAttribute_shouldSetUpTheAttribute() throws Exception {
        SimpleMdsValue<String> firstName = new SimpleMdsValue<>("Bob", DateTime.parse("2012-03-02"), DateTime.parse("2013-09-4"), true);

        Attribute createdAttribute = attributeFactory.createFirstnameAttribute(asList(firstName));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_firstname");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Firstname");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        PersonName firstnameAttributeValue = (PersonName) createdAttribute.getAttributeValues().get(0);
        assertThat(firstnameAttributeValue.getValue()).isEqualTo(firstName.getValue());
        assertThat(firstnameAttributeValue.getFrom()).isEqualTo(firstName.getFrom());
        assertThat(firstnameAttributeValue.getTo()).isEqualTo(firstName.getTo());
        assertThat(firstnameAttributeValue.getLanguage()).isEqualTo("en-GB");
    }

    @Test
    public void createFirstNameAttribute_shouldHandleMultipleValues() throws Exception {
        List<SimpleMdsValue<String>> firstNames = asList(
                SimpleMdsValueBuilder.<String>aSimpleMdsValue().build(),
                SimpleMdsValueBuilder.<String>aSimpleMdsValue().build());

        Attribute createdAttribute = attributeFactory.createFirstnameAttribute(firstNames);

        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(2);
    }

    @Test
    public void createMiddlenameAttribute_shouldSetUpTheAttribute() throws Exception {
        SimpleMdsValue<String> middlename = new SimpleMdsValue<>("Robert", DateTime.parse("2012-03-02"), DateTime.parse("2013-09-4"), false);

        Attribute createdAttribute = attributeFactory.createMiddlenamesAttribute(asList(middlename));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_middlename");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Middlename(s)");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        PersonName middleNameAttributeValue = (PersonName) createdAttribute.getAttributeValues().get(0);
        assertThat(middleNameAttributeValue.getValue()).isEqualTo(middlename.getValue());
        assertThat(middleNameAttributeValue.getFrom()).isEqualTo(middlename.getFrom());
        assertThat(middleNameAttributeValue.getTo()).isEqualTo(middlename.getTo());
        assertThat(middleNameAttributeValue.getLanguage()).isEqualTo("en-GB");
    }

    @Test
    public void createSurnameAttribute_shouldSetUpTheAttribute() throws Exception {
        SimpleMdsValue<String> surname = new SimpleMdsValue<>("McBoberson", DateTime.parse("2012-03-02"), DateTime.parse("2013-09-4"), false);

        Attribute createdAttribute = attributeFactory.createSurnameAttribute(asList(surname));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_surname");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Surname");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        PersonName surnameAttributeValue = (PersonName) createdAttribute.getAttributeValues().get(0);
        assertThat(surnameAttributeValue.getValue()).isEqualTo(surname.getValue());
        assertThat(surnameAttributeValue.getFrom()).isEqualTo(surname.getFrom());
        assertThat(surnameAttributeValue.getTo()).isEqualTo(surname.getTo());
        assertThat(surnameAttributeValue.getLanguage()).isEqualTo("en-GB");
    }

    @Test
    public void createGenderAttribute_shouldSetUpTheAttribute() throws Exception {
        SimpleMdsValue<Gender> genderSimpleMdsValue = new SimpleMdsValue<>(Gender.FEMALE, DateTime.parse("2012-03-02"), DateTime.parse("2013-09-4"), false);

        Attribute createdAttribute = attributeFactory.createGenderAttribute(genderSimpleMdsValue);

        assertThat(createdAttribute.getName()).isEqualTo("MDS_gender");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Gender");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        uk.gov.ida.saml.core.extensions.Gender expectedGenderAttributeValue = (uk.gov.ida.saml.core.extensions.Gender) createdAttribute.getAttributeValues().get(0);
        assertThat(expectedGenderAttributeValue.getValue()).isEqualTo(genderSimpleMdsValue.getValue().getValue());
        assertThat(expectedGenderAttributeValue.getFrom()).isEqualTo(genderSimpleMdsValue.getFrom());
        assertThat(expectedGenderAttributeValue.getTo()).isEqualTo(genderSimpleMdsValue.getTo());
        assertThat(expectedGenderAttributeValue.getVerified()).isEqualTo(genderSimpleMdsValue.isVerified());
    }

    @Test
    public void createCycle3Attribute_shouldSetUpTheAttribute() throws Exception {
        String value = "some value";
        String attributeName = "someAttributeName";
        Attribute createdAttribute = attributeFactory.createCycle3DataAttribute(attributeName, value);

        assertThat(createdAttribute.getName()).isEqualTo(attributeName);
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        assertThat(((StringBasedMdsAttributeValue) createdAttribute.getAttributeValues().get(0)).getValue()).isEqualTo(value);
    }

    @Test
    public void createDateOfBirthAttribute_shouldSetUpTheAttribute() throws Exception {
        SimpleMdsValue<LocalDate> dateOfBirth = new SimpleMdsValue<>(LocalDate.parse("1981-03-29"), DateTime.parse("2012-03-02"), DateTime.parse("2013-09-4"), true);

        Attribute createdAttribute = attributeFactory.createDateOfBirthAttribute(asList(dateOfBirth));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_dateofbirth");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Date of Birth");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);
        uk.gov.ida.saml.core.extensions.Date dateOfBirthAttributeValue = (uk.gov.ida.saml.core.extensions.Date) createdAttribute.getAttributeValues().get(0);
        LocalDate dateOfBirthFromDom = LocalDate.parse(dateOfBirthAttributeValue.getValue());
        assertThat(dateOfBirthFromDom).isEqualTo(dateOfBirth.getValue());
        assertThat(dateOfBirthAttributeValue.getFrom()).isEqualTo(dateOfBirth.getFrom());
        assertThat(dateOfBirthAttributeValue.getTo()).isEqualTo(dateOfBirth.getTo());
        assertThat(dateOfBirthAttributeValue.getVerified()).isEqualTo(dateOfBirth.isVerified());
    }

    @Test
    public void createCurrentAddressAttribute_shouldSetUpTheAttribute() throws Exception {
        String line1Value = "1 Cherry Cottage";
        String line2Value = "Wurpel Lane";
        String postCodeValue = "RG99 1YY";
        String internationalPostCodeValue = "RG88 1ZZ";
        String uprnValue = "RG88 1ZZ";
        DateTime fromDateValue = DateTime.parse("2012-09-09");
        boolean verified = true;
        DateTime toDateValue = DateTime.parse("2012-10-11");
        Address currentAddress = AddressBuilder.anAddress()
                .withLines(asList(line1Value, line2Value))
                .withPostCode(postCodeValue)
                .withInternationalPostCode(internationalPostCodeValue)
                .withUPRN(uprnValue)
                .withFromDate(fromDateValue)
                .withToDate(toDateValue)
                .withVerified(verified)
                .build();

        Attribute createdAttribute = attributeFactory.createCurrentAddressesAttribute(ImmutableList.of(currentAddress));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_currentaddress");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Current Address");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = getAddress(createdAttribute);
        assertThat(addressAttributeValue.getFrom()).isEqualTo(fromDateValue);
        assertThat(addressAttributeValue.getTo()).isEqualTo(toDateValue);
        assertThat(addressAttributeValue.getVerified()).isEqualTo(verified);
        assertThat(addressAttributeValue.getLines().get(0).getValue()).isEqualTo(line1Value);
        assertThat(addressAttributeValue.getLines().get(1).getValue()).isEqualTo(line2Value);
        assertThat(addressAttributeValue.getPostCode().getValue()).isEqualTo(postCodeValue);
        assertThat(addressAttributeValue.getInternationalPostCode().getValue()).isEqualTo(internationalPostCodeValue);
        assertThat(addressAttributeValue.getUPRN().getValue()).isEqualTo(uprnValue);
    }

    @Test
    public void createCurrentAddressAttribute_shouldHandleMissingToDate() throws Exception {
        Address currentAddress = AddressBuilder.anAddress().withLines(asList("Flat 15", "Dalton Tower")).withToDate(null).build();

        attributeFactory.createCurrentAddressesAttribute(ImmutableList.of(currentAddress));
    }

    @Test
    public void createCurrentAddressAttribute_shouldHandleMissingPostCode() throws Exception {
        Address currentAddress = AddressBuilder.anAddress().withPostCode(null).build();

        final Attribute createdAttribute = attributeFactory.createCurrentAddressesAttribute(ImmutableList.of(currentAddress));

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = getAddress(createdAttribute);
        assertThat(addressAttributeValue.getPostCode()).isNull();
    }

    @Test
    public void createCurrentAddressAttribute_shouldHandleMissingInternationalPostCode() throws Exception {
        Address currentAddress = AddressBuilder.anAddress().withInternationalPostCode(null).build();

        final Attribute createdAttribute = attributeFactory.createCurrentAddressesAttribute(ImmutableList.of(currentAddress));

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = getAddress(createdAttribute);
        assertThat(addressAttributeValue.getInternationalPostCode()).isNull();
    }

    @Test
    public void createCurrentAddressAttribute_shouldHandleMissingUPRN() throws Exception {
        Address currentAddress = AddressBuilder.anAddress().withUPRN(null).build();

        final Attribute createdAttribute = attributeFactory.createCurrentAddressesAttribute(ImmutableList.of(currentAddress));

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = getAddress(createdAttribute);
        assertThat(addressAttributeValue.getUPRN()).isNull();
    }

    @Test
    public void createPreviousAddressAttribute_shouldSetUpTheAttribute() throws Exception {
        String line1Value = "1 Cherry Cottage";
        String line2Value = "Wurpel Lane";
        String postCodeValue = "RG99 1YY";
        String internationalPostCodeValue = "RG88 1ZZ";
        DateTime fromDateValue = DateTime.parse("2012-11-12", DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC));
        DateTime toDateValue = DateTime.parse("2012-09-09", DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC));
        String uprnValue = "134279";
        Address previousAddress = AddressBuilder.anAddress()
                .withLines(asList(line1Value, line2Value))
                .withPostCode(postCodeValue)
                .withInternationalPostCode(internationalPostCodeValue)
                .withUPRN(uprnValue)
                .withToDate(toDateValue)
                .withFromDate(fromDateValue)
                .build();

        Attribute createdAttribute = attributeFactory.createPreviousAddressesAttribute(asList(previousAddress));

        assertThat(createdAttribute.getName()).isEqualTo("MDS_previousaddress");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("Previous Address");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);

        uk.gov.ida.saml.core.extensions.Address addressAttributeValue = getAddress(createdAttribute);
        assertThat(addressAttributeValue.getFrom()).isEqualTo(fromDateValue);
        assertThat(addressAttributeValue.getTo()).isEqualTo(toDateValue);
        assertThat(addressAttributeValue.getLines().get(0).getValue()).isEqualTo(line1Value);
        assertThat(addressAttributeValue.getLines().get(1).getValue()).isEqualTo(line2Value);
        assertThat(addressAttributeValue.getPostCode().getValue()).isEqualTo(postCodeValue);
        assertThat(addressAttributeValue.getInternationalPostCode().getValue()).isEqualTo(internationalPostCodeValue);
        assertThat(addressAttributeValue.getUPRN().getValue()).isEqualTo(uprnValue);
    }

    @Test
    public void createPreviousAddressAttribute_shouldHandleMultipleValues() throws Exception {
        List<Address> previousAddresses = asList(AddressBuilder.anAddress().build(), AddressBuilder.anAddress().build());

        Attribute createdAttribute = attributeFactory.createPreviousAddressesAttribute(previousAddresses);

        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(2);
    }

    @Test
    public void createPreviousAddressAttribute_shouldHandleMissingToDate() throws Exception {
        Address previousAddress = AddressBuilder.anAddress().withLines(asList("Flat 15", "Dalton Tower")).withToDate(null).build();

        attributeFactory.createPreviousAddressesAttribute(asList(previousAddress));
    }



    @Test
    public void createGpg45StatusAttribute_shouldSetUpTheAttribute() throws Exception {
        String gpg45Status = "waiting";
        Attribute createdAttribute = attributeFactory.createGpg45StatusAttribute(gpg45Status);

        assertThat(createdAttribute.getName()).isEqualTo("FECI_GPG45Status");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("GPG45Status");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);

        Gpg45Status gpg45StatusAttribute = (Gpg45Status) createdAttribute.getAttributeValues().get(0);

        assertThat(gpg45StatusAttribute.getValue()).isEqualTo(gpg45Status);
    }

    @Test
    public void createIpAddressAttribute_shouldSetUpTheAttribute() throws Exception {
        String ipAddressValue = "0.9.8.7";
        Attribute createdAttribute = attributeFactory.createUserIpAddressAttribute(ipAddressValue);

        assertThat(createdAttribute.getName()).isEqualTo("TXN_IPaddress");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("IPAddress");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);

        IPAddress ipAddressAttributeValue = (IPAddress) createdAttribute.getAttributeValues().get(0);

        assertThat(ipAddressAttributeValue.getValue()).isEqualTo(ipAddressValue);
    }

    @Test
    public void createIdpFraudEventIdAttribute_shouldSetUpTheAttribute() throws Exception {
        String fraudEventId = "fraud-event";
        Attribute createdAttribute = attributeFactory.createIdpFraudEventIdAttribute(fraudEventId);

        assertThat(createdAttribute.getName()).isEqualTo("FECI_IDPFraudEventID");
        assertThat(createdAttribute.getFriendlyName()).isEqualTo("IDPFraudEventID");
        assertThat(createdAttribute.getNameFormat()).isEqualTo(Attribute.UNSPECIFIED);
        assertThat(createdAttribute.getAttributeValues().size()).isEqualTo(1);

        IdpFraudEventId idpFraudEventId = (IdpFraudEventId) createdAttribute.getAttributeValues().get(0);

        assertThat(idpFraudEventId.getValue()).isEqualTo(fraudEventId);
    }

    private uk.gov.ida.saml.core.extensions.Address getAddress(Attribute createdAttribute) {
        List<XMLObject> addressAttributeValues = createdAttribute.getAttributeValues();
        assertThat(addressAttributeValues.size()).isEqualTo(1);
        return (uk.gov.ida.saml.core.extensions.Address) addressAttributeValues.get(0);
    }
}
