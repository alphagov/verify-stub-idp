package uk.gov.ida.saml.hub.transformers.outbound;

import java.util.Optional;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.HubAssertion;
import uk.gov.ida.saml.hub.domain.HubAttributeQueryRequest;
import uk.gov.ida.saml.hub.domain.UserAccountCreationAttribute;
import uk.gov.ida.saml.hub.factories.AttributeQueryAttributeFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class HubAttributeQueryRequestToSamlAttributeQueryTransformer implements Function<HubAttributeQueryRequest,AttributeQuery> {

    private final OpenSamlXmlObjectFactory samlObjectFactory;
    private final HubAssertionMarshaller hubAssertionMarshaller;
    private final AttributeQueryAttributeFactory attributeQueryAttributeFactory;
    private final EncryptedAssertionUnmarshaller encryptedAssertionUnmarshaller;

    public HubAttributeQueryRequestToSamlAttributeQueryTransformer(
            final OpenSamlXmlObjectFactory samlObjectFactory,
            final HubAssertionMarshaller hubAssertionMarshaller,
            final AttributeQueryAttributeFactory attributeQueryAttributeFactory,
            final EncryptedAssertionUnmarshaller encryptedAssertionUnmarshaller) {

        this.samlObjectFactory = samlObjectFactory;
        this.hubAssertionMarshaller = hubAssertionMarshaller;
        this.attributeQueryAttributeFactory = attributeQueryAttributeFactory;
        this.encryptedAssertionUnmarshaller = encryptedAssertionUnmarshaller;
    }

    public AttributeQuery apply(HubAttributeQueryRequest originalQuery) {
        AttributeQuery transformedQuery = samlObjectFactory.createAttributeQuery();

        Issuer issuer = samlObjectFactory.createIssuer(originalQuery.getIssuer());

        transformedQuery.setID(originalQuery.getId());
        transformedQuery.setIssuer(issuer);
        transformedQuery.setIssueInstant(DateTime.now());

        if (originalQuery.getUserAccountCreationAttributes().isPresent()){
            transformedQuery.getAttributes().addAll(createAttributeList(originalQuery.getUserAccountCreationAttributes().get()));
        }

        Subject subject = samlObjectFactory.createSubject();
        NameID nameId = samlObjectFactory.createNameId(originalQuery.getPersistentId().getNameId());
        nameId.setSPNameQualifier(originalQuery.getAuthnRequestIssuerEntityId());
        nameId.setNameQualifier(originalQuery.getAssertionConsumerServiceUrl().toASCIIString());
        subject.setNameID(nameId);

        SubjectConfirmation subjectConfirmation = samlObjectFactory.createSubjectConfirmation();
        SubjectConfirmationData subjectConfirmationData = samlObjectFactory.createSubjectConfirmationData();

        Stream.of(originalQuery.getEncryptedMatchingDatasetAssertion(), originalQuery.getEncryptedAuthnAssertion())
                .map(encryptedAssertionUnmarshaller::transform)
                .forEach(subjectConfirmationData.getUnknownXMLObjects(EncryptedAssertion.DEFAULT_ELEMENT_NAME)::add);

        final Optional<HubAssertion> cycle3DatasetAssertion = originalQuery.getCycle3AttributeAssertion();
        if (cycle3DatasetAssertion.isPresent()) {
            Assertion transformedCycle3DatasetAssertion = hubAssertionMarshaller.toSaml(cycle3DatasetAssertion.get());
            subjectConfirmationData.getUnknownXMLObjects(Assertion.DEFAULT_ELEMENT_NAME).add(transformedCycle3DatasetAssertion);
        }

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        subject.getSubjectConfirmations().add(subjectConfirmation);

        transformedQuery.setSubject(subject);

        return transformedQuery;
    }

    private List<Attribute> createAttributeList(List<UserAccountCreationAttribute> userAccountCreationAttributes) {
        return userAccountCreationAttributes
                .stream()
                .map(attributeQueryAttributeFactory::createAttribute)
                .collect(Collectors.toList());
    }

}
