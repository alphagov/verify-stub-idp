package uk.gov.ida.saml.idp.stub.tranformers.outbound;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.test.builders.MatchingDatasetBuilder;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.builders.SimpleMdsValueBuilder;
import uk.gov.ida.saml.core.transformers.outbound.OutboundAssertionToSubjectTransformer;
import uk.gov.ida.saml.hub.factories.AttributeFactory;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAssertionToAssertionTransformer;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAuthnStatementToAuthnStatementTransformer;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.ida.saml.idp.test.builders.IPAddressAttributeBuilder.anIPAddress;
import static uk.gov.ida.saml.idp.test.builders.IdentityProviderAssertionBuilder.anIdentityProviderAssertion;
import static uk.gov.ida.saml.core.test.builders.IdentityProviderAuthnStatementBuilder.anIdentityProviderAuthnStatement;
import static uk.gov.ida.saml.core.test.builders.IpAddressBuilder.anIpAddress;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdentityProviderAssertionToAssertionTransformerTest {

    private IdentityProviderAssertionToAssertionTransformer transformer;
    @Mock
    private AttributeFactory attributeFactory;
    @Mock
    private IdentityProviderAuthnStatementToAuthnStatementTransformer identityProviderAuthnStatementToAuthnStatementTransformer;
    @Mock
    private OutboundAssertionToSubjectTransformer outboundAssertionToSubjectTransformer;

    private final Address currentAddress = new AddressFactory().create(asList("subject-address-line-1"), "subject-address-post-code", "internation-postcode", "uprn", DateTime.parse("1999-03-15"), DateTime.parse("2000-02-09"), true);
    private final Address previousAddress = new AddressFactory().create(asList("subject-address-line-1"), "subject-address-post-code", "internation-postcode", "uprn", DateTime.parse("1999-03-15"), DateTime.parse("2000-02-09"), true);
    private final SimpleMdsValue<String> previousSurname = SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-previousSurname").withVerifiedStatus(true).build();
    private final SimpleMdsValue<String> currentSurname = SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-currentSurname").withVerifiedStatus(true).build();

    @Before
    public void setup() {
        OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
        transformer = new IdentityProviderAssertionToAssertionTransformer(
                openSamlXmlObjectFactory,
                attributeFactory,
                identityProviderAuthnStatementToAuthnStatementTransformer,
                outboundAssertionToSubjectTransformer);
    }

    @Test
    public void transform_shouldTransformAssertionSubjects() throws Exception {
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().build();

        transformer.transform(assertion);

        verify(outboundAssertionToSubjectTransformer).transform(assertion);
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsFirstName() throws Exception {
        SimpleMdsValue<String> firstname = SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("Bob").build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().addFirstname(firstname).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createFirstnameAttribute(assertion.getMatchingDataset().get().getFirstNames());
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsFirstname() throws Exception {
        MatchingDataset matchingDataset = MatchingDatasetBuilder.aMatchingDataset()
                .addMiddleNames(SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-middlename").withVerifiedStatus(true).build())
                .withSurnameHistory(asList(previousSurname, currentSurname))
                .withGender(SimpleMdsValueBuilder.<Gender>aSimpleMdsValue().withValue(Gender.FEMALE).withVerifiedStatus(true).build())
                .addDateOfBirth(SimpleMdsValueBuilder.<LocalDate>aSimpleMdsValue().withValue(LocalDate.parse("2000-02-09")).withVerifiedStatus(true).build())
                .withCurrentAddresses(asList(currentAddress))
                .withPreviousAddresses(asList(previousAddress))
                .build();

        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(matchingDataset).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createFirstnameAttribute(Matchers.<List<SimpleMdsValue<String>>>any());
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsMiddleNames() throws Exception {
        SimpleMdsValue<String> middleNames = SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("archibald ferdinand").build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().addMiddleNames(middleNames).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createMiddlenamesAttribute(assertion.getMatchingDataset().get().getMiddleNames());
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsMiddleNames() throws Exception {
        MatchingDataset matchingDataset = MatchingDatasetBuilder.aMatchingDataset()
                .addFirstname(SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-firstname").withVerifiedStatus(true).build())
                .withSurnameHistory(asList(previousSurname, currentSurname))
                .withGender(SimpleMdsValueBuilder.<Gender>aSimpleMdsValue().withValue(Gender.FEMALE).withVerifiedStatus(true).build())
                .addDateOfBirth(SimpleMdsValueBuilder.<LocalDate>aSimpleMdsValue().withValue(LocalDate.parse("2000-02-09")).withVerifiedStatus(true).build())
                .withCurrentAddresses(asList(currentAddress))
                .withPreviousAddresses(asList(previousAddress))
                .build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(matchingDataset).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createMiddlenamesAttribute(Matchers.<List<SimpleMdsValue<String>>>any());
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsSurname() throws Exception {
        SimpleMdsValue<String> surname = SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("Cratchit").build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().addSurname(surname).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createSurnameAttribute(assertion.getMatchingDataset().get().getSurnames());
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsSurname() throws Exception {
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aFullyPopulatedMatchingDataset().withoutSurname().build()).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createSurnameAttribute(Matchers.<List<SimpleMdsValue<String>>>any());
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsGender() throws Exception {
        SimpleMdsValue<Gender> gender = SimpleMdsValueBuilder.<Gender>aSimpleMdsValue().withValue(Gender.FEMALE).build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().withGender(gender).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createGenderAttribute(gender);
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsGender() throws Exception {
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aFullyPopulatedMatchingDataset().withGender(null).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createGenderAttribute(Matchers.<SimpleMdsValue<Gender>>any());
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsDateOfBirth() throws Exception {
        SimpleMdsValue<LocalDate> dateOfBirth = SimpleMdsValueBuilder.<LocalDate>aSimpleMdsValue().withValue(LocalDate.parse("1986-12-05")).build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().addDateOfBirth(dateOfBirth).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createDateOfBirthAttribute(assertion.getMatchingDataset().get().getDateOfBirths());
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsDateOfBirth() throws Exception {
        MatchingDataset matchingDataset = MatchingDatasetBuilder.aMatchingDataset()
                .addFirstname(SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-firstname").withVerifiedStatus(true).build())
                .addMiddleNames(SimpleMdsValueBuilder.<String>aSimpleMdsValue().withValue("subject-middlename").withVerifiedStatus(true).build())
                .withSurnameHistory(asList(previousSurname, currentSurname))
                .withGender(SimpleMdsValueBuilder.<Gender>aSimpleMdsValue().withValue(Gender.FEMALE).withVerifiedStatus(true).build())
                .withCurrentAddresses(asList(currentAddress))
                .withPreviousAddresses(asList(previousAddress))
                .build();

        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(matchingDataset).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createDateOfBirthAttribute(Matchers.<List<SimpleMdsValue<LocalDate>>>any());
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsCurrentAddress() throws Exception {
        List<Address> address = asList(new AddressFactory().create(asList("221b Baker St."), "W4 1SH", "A 1", "4536789", DateTime.parse("2007-09-28"), DateTime.parse("2007-10-29"), true));
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().withCurrentAddresses(address).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createCurrentAddressesAttribute(address);
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsCurrentAddress() throws Exception {
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aFullyPopulatedMatchingDataset().withCurrentAddresses(new ArrayList<Address>()).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createCurrentAddressesAttribute(anyListOf(Address.class));
    }

    @Test
    public void transform_shouldTransformAssertionSubjectsPreviousAddresses() throws Exception {
        Address previousAddressOne = new AddressFactory().create(asList("221b Baker St."), "W4 1SH", null, null, DateTime.parse("2007-09-27"), DateTime.parse("2007-09-28"), true);
        Address previousAddressTwo = new AddressFactory().create(asList("1 Goose Lane"), "M1 2FG", null, null, DateTime.parse("2006-09-29"), DateTime.parse("2006-09-28"), false);
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aMatchingDataset().withPreviousAddresses(asList(previousAddressOne, previousAddressTwo)).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory).createPreviousAddressesAttribute(asList(previousAddressOne, previousAddressTwo));
    }

    @Test
    public void transform_shouldHandleMissingAssertionSubjectsPreviousAddress() throws Exception {
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withMatchingDataset(MatchingDatasetBuilder.aFullyPopulatedMatchingDataset().withPreviousAddresses(new ArrayList<Address>()).build()).build();

        transformer.transform(assertion);

        verify(attributeFactory, never()).createPreviousAddressesAttribute(anyListOf(Address.class));
    }

    @Test
    public void transform_shouldTransformAssertionId() throws Exception {
        String assertionId = "assertion-id";
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withId(assertionId).build();

        Assertion transformedAssertion = transformer.transform(assertion);

        assertThat(transformedAssertion.getID()).isEqualTo(assertionId);
    }

    @Test
    public void transform_shouldTransformAssertionIssuer() throws Exception {
        String assertionIssuerId = "assertion issuer";
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withIssuer(assertionIssuerId).build();

        Assertion transformedAssertion = transformer.transform(assertion);

        assertThat(transformedAssertion.getIssuer().getValue()).isEqualTo(assertionIssuerId);
    }

    @Test
    public void transform_shouldTransformAssertionIssuerInstance() throws Exception {
        DateTime issueInstant = DateTime.parse("2012-12-31T12:34:56Z");
        IdentityProviderAssertion assertion = anIdentityProviderAssertion().withIssueInstant(issueInstant).build();

        Assertion transformedAssertion = transformer.transform(assertion);

        assertThat(transformedAssertion.getIssueInstant()).isEqualTo(issueInstant);
    }

    @Test
    public void transform_shouldTransformLevelOfAssurance() throws Exception {
        AuthnContext levelOfAssurance = AuthnContext.LEVEL_2;
        IdentityProviderAuthnStatement authnStatement = anIdentityProviderAuthnStatement()
                .withAuthnContext(levelOfAssurance)
                .build();
        IdentityProviderAssertion assertion = anIdentityProviderAssertion()
                .withAuthnStatement(authnStatement)
                .build();

        transformer.transform(assertion);

        verify(identityProviderAuthnStatementToAuthnStatementTransformer).transform(authnStatement);
    }

    @Test
    public void transform_shouldTransformFraudDetailsEventId() throws Exception {
        String reference = "reference";
        FraudAuthnDetails fraudAuthnDetails = new FraudAuthnDetails(reference, "IT01");
        IdentityProviderAssertion assertion = anIdentityProviderAssertion()
                .withAuthnStatement(anIdentityProviderAuthnStatement().withFraudDetails(fraudAuthnDetails).build())
                .build();

        transformer.transform(assertion);

        verify(attributeFactory).createIdpFraudEventIdAttribute(reference);
    }

    @Test
    public void transform_shouldTransformFraudDetailsIndicatorIfPresent() throws Exception {
        String indicator = "FI01";
        FraudAuthnDetails fraudAuthnDetails = new FraudAuthnDetails("ref", "FI01");
        IdentityProviderAssertion assertion = anIdentityProviderAssertion()
                .withAuthnStatement(anIdentityProviderAuthnStatement().withFraudDetails(fraudAuthnDetails).build())
                .build();

        transformer.transform(assertion);

        verify(attributeFactory).createGpg45StatusAttribute(indicator);
    }

    @Test
    public void transform_shouldTransformIpAddress() throws Exception {
        String ipAddressValue = "9.9.8.8";
        IdentityProviderAssertion assertion = anIdentityProviderAssertion()
                .withAuthnStatement(anIdentityProviderAuthnStatement().withUserIpAddress(anIpAddress().withValue(ipAddressValue).build()).build())
                .build();
        final Attribute attribute = anIPAddress().withValue("4.5.6.7").build();
        when(attributeFactory.createUserIpAddressAttribute(ipAddressValue)).thenReturn(attribute);

        final Assertion transformedAssertion = transformer.transform(assertion);

        final Attribute ipAddressAttribute = transformedAssertion.getAttributeStatements().get(0).getAttributes().get(0);
        assertThat(ipAddressAttribute).isEqualTo(attribute);
    }
}
