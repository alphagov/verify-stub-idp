package uk.gov.ida.saml.hub.factories;

import org.joda.time.LocalDate;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;

import java.util.List;

public interface AttributeFactory {
    Attribute createFirstnameAttribute(List<SimpleMdsValue<String>> firstnames);

    Attribute createMiddlenamesAttribute(List<SimpleMdsValue<String>> middlenames);

    Attribute createSurnameAttribute(List<SimpleMdsValue<String>> surname);

    Attribute createGenderAttribute(SimpleMdsValue<Gender> gender);

    Attribute createDateOfBirthAttribute(List<SimpleMdsValue<LocalDate>> dateOfBirths);

    Attribute createCurrentAddressesAttribute(List<Address> currentAddresses);

    Attribute createPreviousAddressesAttribute(List<Address> previousAddresses);

    Attribute createCycle3DataAttribute(String attributeName, String cycle3Data);

    Attribute createIdpFraudEventIdAttribute(String eventId);

    Attribute createGpg45StatusAttribute(String indicator);

    Attribute createUserIpAddressAttribute(String userIpAddress);
}
