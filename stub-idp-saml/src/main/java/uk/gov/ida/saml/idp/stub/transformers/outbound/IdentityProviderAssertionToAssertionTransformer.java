package uk.gov.ida.saml.idp.stub.transformers.outbound;

import com.google.inject.Inject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Issuer;
import uk.gov.ida.saml.core.domain.FraudAuthnDetails;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.domain.IpAddress;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.transformers.outbound.OutboundAssertionToSubjectTransformer;
import uk.gov.ida.saml.hub.factories.AttributeFactory;

import java.util.ArrayList;
import java.util.Collection;

public class IdentityProviderAssertionToAssertionTransformer {

    private final OpenSamlXmlObjectFactory openSamlXmlObjectFactory;
    private final AttributeFactory attributeFactory;
    private final IdentityProviderAuthnStatementToAuthnStatementTransformer identityProviderAuthnStatementToAuthnStatementTransformer;
    private final OutboundAssertionToSubjectTransformer outboundAssertionToSubjectTransformer;

    @Inject
    public IdentityProviderAssertionToAssertionTransformer(
            OpenSamlXmlObjectFactory openSamlXmlObjectFactory,
            AttributeFactory attributeFactory,
            IdentityProviderAuthnStatementToAuthnStatementTransformer identityProviderAuthnStatementToAuthnStatementTransformer,
            OutboundAssertionToSubjectTransformer outboundAssertionToSubjectTransformer) {

        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
        this.attributeFactory = attributeFactory;
        this.identityProviderAuthnStatementToAuthnStatementTransformer = identityProviderAuthnStatementToAuthnStatementTransformer;
        this.outboundAssertionToSubjectTransformer = outboundAssertionToSubjectTransformer;
    }

    public Assertion transform(IdentityProviderAssertion identityProviderAssertion) {

        Assertion transformedAssertion = openSamlXmlObjectFactory.createAssertion();
        transformedAssertion.setIssueInstant(identityProviderAssertion.getIssueInstant());
        Issuer transformedIssuer = openSamlXmlObjectFactory.createIssuer(identityProviderAssertion.getIssuerId());
        transformedAssertion.setIssuer(transformedIssuer);
        transformedAssertion.setID(identityProviderAssertion.getId());

        transformedAssertion.setSubject(outboundAssertionToSubjectTransformer.transform(identityProviderAssertion));

        if (identityProviderAssertion.getMatchingDataset().isPresent()) {
            transformedAssertion.getAttributeStatements().add(transform(identityProviderAssertion.getMatchingDataset().get()));
        }

        if (identityProviderAssertion.getAuthnStatement().isPresent()) {
            IdentityProviderAuthnStatement authnStatement = identityProviderAssertion.getAuthnStatement().get();

            transformedAssertion.getAuthnStatements().add(identityProviderAuthnStatementToAuthnStatementTransformer.transform(authnStatement));

            AttributeStatement attributeStatement = openSamlXmlObjectFactory.createAttributeStatement();
            transformedAssertion.getAttributeStatements().add(attributeStatement);

            if (authnStatement.isFraudAuthnStatement()) {
                attributeStatement.getAttributes().addAll(createFraudAttributes(authnStatement.getFraudAuthnDetails()));
            }

            attributeStatement.getAttributes().add(createIpAddressAttribute(authnStatement.getUserIpAddress()));
        }

        return transformedAssertion;
    }

    private Attribute createIpAddressAttribute(IpAddress userIpAddress) {
        return attributeFactory.createUserIpAddressAttribute(userIpAddress.getStringValue());
    }

    private Collection<Attribute> createFraudAttributes(FraudAuthnDetails fraudAuthnDetails) {
        Collection<Attribute> attributes = new ArrayList<>();

        Attribute idpFraudEventId = attributeFactory.createIdpFraudEventIdAttribute(fraudAuthnDetails.getEventId());
        attributes.add(idpFraudEventId);

        String indicator = fraudAuthnDetails.getFraudIndicator();
        Attribute fraudIndicator = attributeFactory.createGpg45StatusAttribute(indicator);
        attributes.add(fraudIndicator);

        return attributes;
    }

    private AttributeStatement transform(MatchingDataset matchingDataset) {
        AttributeStatement attributeStatement = openSamlXmlObjectFactory.createAttributeStatement();

        if (!matchingDataset.getFirstNames().isEmpty()) {
            Attribute firstnameAttribute = attributeFactory.createFirstnameAttribute(matchingDataset.getFirstNames());
            attributeStatement.getAttributes().add(firstnameAttribute);
        }

        if (!matchingDataset.getMiddleNames().isEmpty()) {
            Attribute middleNamesAttribute = attributeFactory.createMiddlenamesAttribute(matchingDataset.getMiddleNames());
            attributeStatement.getAttributes().add(middleNamesAttribute);
        }

        if (!matchingDataset.getSurnames().isEmpty()) {
            Attribute surnameAttribute = attributeFactory.createSurnameAttribute(matchingDataset.getSurnames());
            attributeStatement.getAttributes().add(surnameAttribute);
        }

        if (matchingDataset.getGender().isPresent()) {
            Attribute genderAttribute = attributeFactory.createGenderAttribute(matchingDataset.getGender().get());
            attributeStatement.getAttributes().add(genderAttribute);
        }

        if (!matchingDataset.getDateOfBirths().isEmpty()) {
            Attribute dateOfBirthAttribute = attributeFactory.createDateOfBirthAttribute(matchingDataset.getDateOfBirths());
            attributeStatement.getAttributes().add(dateOfBirthAttribute);
        }

        if (matchingDataset.getCurrentAddresses().size() > 0) {
            Attribute currentAddressesAttribute = attributeFactory.createCurrentAddressesAttribute(matchingDataset.getCurrentAddresses());
            attributeStatement.getAttributes().add(currentAddressesAttribute);
        }

        if (matchingDataset.getPreviousAddresses().size() > 0) {
            Attribute previousAddressesAttribute = attributeFactory.createPreviousAddressesAttribute(matchingDataset.getPreviousAddresses());
            attributeStatement.getAttributes().add(previousAddressesAttribute);
        }

        return attributeStatement;
    }

}
