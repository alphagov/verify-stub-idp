package uk.gov.ida.saml.core.transformers;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.extensions.eidas.Gender;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentFamilyNameBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentGivenNameBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.DateOfBirthBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.GenderBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.PersonIdentifierBuilder;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anEidasMatchingDatasetAssertion;
import static uk.gov.ida.saml.core.test.builders.PersonIdentifierAttributeBuilder.aPersonIdentifier;

@RunWith(OpenSAMLMockitoRunner.class)
public class EidasMatchingDatasetUnmarshallerTest {

    private EidasMatchingDatasetUnmarshaller unmarshaller;
    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    @Before
    public void setUp() {
        this.unmarshaller = new EidasMatchingDatasetUnmarshaller();
    }

    @Test
    public void transformShouldTransformAnAssertionIntoAMatchingDataset() {
        Attribute firstname = anEidasFirstName("Bob", true);
        Attribute surname = anEidasFamilyName("Bobbins", true);
        LocalDate dob = new LocalDate(1986, 12, 05);
        Attribute dateOfBirth = anEidasDateOfBirth(dob);

        PersonIdentifier personIdentifier = new PersonIdentifierBuilder().buildObject();
        personIdentifier.setPersonIdentifier("PID12345");
        Attribute personalIdentifier = aPersonIdentifier().withValue(personIdentifier).build();
        Assertion originalAssertion = anEidasMatchingDatasetAssertion(firstname, surname, dateOfBirth, personalIdentifier,
                Optional.empty()).buildUnencrypted();

        MatchingDataset matchingDataset = unmarshaller.fromAssertion(originalAssertion);

        assertThat(matchingDataset.getFirstNames().get(0).getValue()).isEqualTo("Bob");
        assertThat(matchingDataset.getSurnames().get(0).getValue()).isEqualTo("Bobbins");
        assertThat(matchingDataset.getDateOfBirths().get(0).getValue()).isEqualTo(dob);
        assertThat(matchingDataset.getPersonalId()).isEqualTo("PID12345");

        assertThat(matchingDataset.getFirstNames().get(0).isVerified()).isTrue();
        assertThat(matchingDataset.getSurnames().get(0).isVerified()).isTrue();
        assertThat(matchingDataset.getDateOfBirths().get(0).isVerified()).isTrue();
    }

    @Test
    public void transformShouldTransformAnAssertionIntoAMatchingDatasetWithNonLatinNames() {
        Attribute firstname = anEidasFirstName("Bob", true);
        firstname.getAttributeValues().add(getCurrentGivenName("Βαρίδι", false));
        Attribute surname = anEidasFamilyName("Smith", true);
        surname.getAttributeValues().add(getCurrentFamilyName("Σιδηρουργός", false));
        LocalDate dob = new LocalDate(1986, 12, 05);
        Attribute dateOfBirth = anEidasDateOfBirth(dob);

        // Ensure that the unmarshaller does not error when provided a gender
        Attribute gender = anEidasGender(uk.gov.ida.saml.core.domain.Gender.MALE.getValue());

        PersonIdentifier personIdentifier = new PersonIdentifierBuilder().buildObject();
        personIdentifier.setPersonIdentifier("PID12345");
        Attribute personalIdentifier = aPersonIdentifier().withValue(personIdentifier).build();
        Assertion originalAssertion = anEidasMatchingDatasetAssertion(firstname, surname, dateOfBirth, personalIdentifier,
                Optional.of(gender)).buildUnencrypted();

        MatchingDataset matchingDataset = unmarshaller.fromAssertion(originalAssertion);

        assertThat(matchingDataset.getFirstNames().get(0).getValue()).isEqualTo("Bob");
        assertThat(matchingDataset.getFirstNames().get(0).getNonLatinScriptValue()).isEqualTo("Βαρίδι");
        assertThat(matchingDataset.getSurnames().get(0).getValue()).isEqualTo("Smith");
        assertThat(matchingDataset.getSurnames().get(0).getNonLatinScriptValue()).isEqualTo("Σιδηρουργός");
        assertThat(matchingDataset.getDateOfBirths().get(0).getValue()).isEqualTo(dob);
        assertThat(matchingDataset.getPersonalId()).isEqualTo("PID12345");
        assertThat(matchingDataset.getGender()).isNotPresent();

        assertThat(matchingDataset.getFirstNames().get(0).isVerified()).isTrue();
        assertThat(matchingDataset.getSurnames().get(0).isVerified()).isTrue();
        assertThat(matchingDataset.getDateOfBirths().get(0).isVerified()).isTrue();
    }

    private Attribute anEidasAttribute(String name, AttributeValue value) {
        Attribute attribute = openSamlXmlObjectFactory.createAttribute();
        attribute.setName(name);
        attribute.getAttributeValues().add(value);
        return attribute;
    }

    private Attribute anEidasFirstName(String firstName, boolean isLatinScript) {
        CurrentGivenName firstNameValue = getCurrentGivenName(firstName, isLatinScript);
        return anEidasAttribute(IdaConstants.Eidas_Attributes.FirstName.NAME, firstNameValue);
    }

    private CurrentGivenName getCurrentGivenName(String firstName, boolean isLatinScript) {
        CurrentGivenName firstNameValue = new CurrentGivenNameBuilder().buildObject();
        firstNameValue.setFirstName(firstName);
        firstNameValue.setIsLatinScript(isLatinScript);
        return firstNameValue;
    }

    private Attribute anEidasFamilyName(String familyName, boolean isLatinScript) {
        CurrentFamilyName currentFamilyName = getCurrentFamilyName(familyName, isLatinScript);
        return anEidasAttribute(IdaConstants.Eidas_Attributes.FamilyName.NAME, currentFamilyName);
    }

    private CurrentFamilyName getCurrentFamilyName(String familyName, boolean isLatinScript) {
        CurrentFamilyName currentFamilyName = new CurrentFamilyNameBuilder().buildObject();
        currentFamilyName.setFamilyName(familyName);
        currentFamilyName.setIsLatinScript(isLatinScript);
        return currentFamilyName;
    }

    private Attribute anEidasDateOfBirth(LocalDate dob) {
        DateOfBirth dateOfBirth = new DateOfBirthBuilder().buildObject();
        dateOfBirth.setDateOfBirth(dob);
        return anEidasAttribute(IdaConstants.Eidas_Attributes.DateOfBirth.NAME, dateOfBirth);
    }

    private Attribute anEidasGender(String gender) {
        Gender genderValue = new GenderBuilder().buildObject();
        genderValue.setValue(gender);
        return anEidasAttribute(IdaConstants.Eidas_Attributes.Gender.NAME, genderValue);
    }
}
