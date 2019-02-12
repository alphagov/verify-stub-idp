package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestCredentialFactory;
import uk.gov.ida.saml.core.test.TestEntityIds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;
import static uk.gov.ida.saml.core.test.TestEntityIds.HUB_CONNECTOR_ENTITY_ID;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anEidasAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.AuthnStatementBuilder.anEidasAuthnStatement;
import static uk.gov.ida.saml.core.test.builders.SubjectBuilder.aSubject;
import static uk.gov.ida.saml.core.test.builders.SubjectConfirmationBuilder.aSubjectConfirmation;
import static uk.gov.ida.saml.core.test.builders.SubjectConfirmationDataBuilder.aSubjectConfirmationData;

public class AssertionBuilder {

    private static XMLObjectBuilderFactory factory = XMLObjectProviderRegistrySupport.getBuilderFactory();

    private boolean shouldSign = true;
    private SAMLVersion version = SAMLVersion.VERSION_20;
    private List<AttributeStatement> attributeStatements = new ArrayList<>();
    private List<AuthnStatement> authnStatements = new ArrayList<>();

    private Optional<String> id = Optional.of("some-assertion-id");
    private Optional<Subject> subject = Optional.ofNullable(aSubject().build());
    private Optional<Issuer> issuer = Optional.ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<Signature> signature = Optional.ofNullable(SignatureBuilder.aSignature().build());
    private Optional<Conditions> conditions = Optional.ofNullable(ConditionsBuilder.aConditions().build());
    private Optional<DateTime> issueInstant = Optional.of(DateTime.now());

    public static AssertionBuilder anAssertion() {
        return new AssertionBuilder();
    }

    public static AssertionBuilder anEidasAssertion() {
        return anAssertion()
            .withConditions(
                new ConditionsBuilder()
                .validFor(Duration.standardMinutes(10))
                .restrictedToAudience(HUB_CONNECTOR_ENTITY_ID)
                .build())
            .withIssuer(
                new IssuerBuilder()
                .withIssuerId(TestEntityIds.STUB_COUNTRY_ONE)
                .build())
            .addAttributeStatement(anEidasAttributeStatement().build())
            .addAuthnStatement(anEidasAuthnStatement().build())
            .withSubject(
                    aSubject()
                    .withSubjectConfirmation(
                        aSubjectConfirmation()
                        .withSubjectConfirmationData(aSubjectConfirmationData().withRecipient(HUB_CONNECTOR_ENTITY_ID).build())
                        .build())
                    .build());
    }

    public static AssertionBuilder anAuthnStatementAssertion() {
        return anAssertion()
            .addAuthnStatement(AuthnStatementBuilder.anAuthnStatement().build())
            .addAttributeStatement(anAttributeStatement().addAttribute(IPAddressAttributeBuilder.anIPAddress().build()).build());
    }

    public static AssertionBuilder aMatchingDatasetAssertion(
        Attribute firstName,
        Attribute middlenames,
        Attribute surname,
        Attribute gender,
        Attribute dateOfBirth,
        Attribute currentAddress,
        Attribute previousAddresses) {
        AttributeStatementBuilder attributeStatementBuilder = anAttributeStatement()
            .addAttribute(firstName)
            .addAttribute(middlenames)
            .addAttribute(surname)
            .addAttribute(gender)
            .addAttribute(dateOfBirth)
            .addAttribute(currentAddress)
            .addAttribute(previousAddresses);

        return anAssertion()
            .addAttributeStatement(attributeStatementBuilder.build());
    }

    public static AssertionBuilder anEidasMatchingDatasetAssertion(
            Attribute firstName,
            Attribute surname,
            Attribute dateOfBirth,
            Attribute personalIdentifier) {
        return anEidasMatchingDatasetAssertion(firstName, surname, dateOfBirth, personalIdentifier, Optional.empty());
    }

    public static AssertionBuilder anEidasMatchingDatasetAssertion(
            Attribute firstName,
            Attribute surname,
            Attribute dateOfBirth,
            Attribute personalIdentifier,
            Optional<Attribute> gender) {
        AttributeStatementBuilder attributeStatementBuilder = anAttributeStatement()
            .addAttribute(firstName)
            .addAttribute(surname)
            .addAttribute(dateOfBirth)
            .addAttribute(personalIdentifier);

        gender.ifPresent(attributeStatementBuilder::addAttribute);

        return anAssertion()
            .addAttributeStatement(attributeStatementBuilder.build());
    }

    public static AssertionBuilder aCycle3DatasetAssertion(String name, String value) {
        SimpleStringAttributeBuilder attribute = SimpleStringAttributeBuilder.aSimpleStringAttribute()
            .withName(name)
            .withSimpleStringValue(value);

        AttributeStatementBuilder attributeStatementBuilder = anAttributeStatement()
            .addAttribute(attribute.build());

        return anAssertion()
            .withIssuer(IssuerBuilder.anIssuer().withIssuerId(TestEntityIds.HUB_ENTITY_ID).build()) // Hub entity id defines the ability to parse an attribute query
            .addAttributeStatement(attributeStatementBuilder.build());
    }

    public static AssertionBuilder aCycle3DatasetAssertion(List<Attribute> attributes) {
        AttributeStatementBuilder attributeStatementBuilder = anAttributeStatement();
        for (Attribute attribute : attributes) {
            attributeStatementBuilder.addAttribute(attribute);
        }
        return anAssertion()
            .withIssuer(IssuerBuilder.anIssuer().withIssuerId(TestEntityIds.HUB_ENTITY_ID).build()) // Hub entity id defines the ability to parse an attribute query
            .addAttributeStatement(attributeStatementBuilder.build());
    }

    public Assertion buildUnencrypted() {
        Assertion assertion = (Assertion) factory
            .getBuilder(Assertion.DEFAULT_ELEMENT_NAME)
            .buildObject(Assertion.DEFAULT_ELEMENT_NAME, Assertion.TYPE_NAME);

        if (id.isPresent()) {
            assertion.setID(id.get());
        }

        assertion.setVersion(version);

        if (subject.isPresent()) {
            assertion.setSubject(subject.get());
        }

        if (issueInstant.isPresent()) {
            assertion.setIssueInstant(issueInstant.get());
        }

        assertion.getAttributeStatements().addAll(attributeStatements);
        assertion.getAuthnStatements().addAll(authnStatements);

        if (issuer.isPresent()) {
            assertion.setIssuer(issuer.get());
        }
        if (conditions.isPresent()) {
            assertion.setConditions(conditions.get());
        }
        try {
            if (signature.isPresent()) {
                assertion.setSignature(signature.get());
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
                if (shouldSign) {
                    Signer.signObject(assertion.getSignature());
                }
            }
        } catch (SignatureException | MarshallingException e) {
            throw propagate(e);
        }

        return assertion;
    }

    public EncryptedAssertion build() {
        TestCredentialFactory credentialFactory = new TestCredentialFactory(TestCertificateStrings.TEST_PUBLIC_CERT, null);
        Credential credential = credentialFactory.getEncryptingCredential();

        return buildWithEncrypterCredential(credential);
    }

    public EncryptedAssertion buildWithEncrypterCredential(Credential credential) {
        return buildWithEncrypterCredential(createEncrypter(credential));
    }

    public EncryptedAssertion buildWithEncrypterCredential(Credential credential, String encryptionAlgorithm) {
        return buildWithEncrypterCredential(createEncrypter(credential, encryptionAlgorithm));
    }

    public EncryptedAssertion buildWithEncrypterCredential(Credential credential,
                                                           String encryptionAlgorithm,
                                                           String keyTransportEncryptionAlgorithm) {
        return buildWithEncrypterCredential(createEncrypter(credential, encryptionAlgorithm, keyTransportEncryptionAlgorithm));
    }

    public Encrypter createEncrypter(Credential credential) {
        return createEncrypter(credential, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
    }

    public Encrypter createEncrypter(Credential credential, String encryptionAlgorithm) {
        return createEncrypter(credential, encryptionAlgorithm, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
    }

    public Encrypter createEncrypter(Credential credential, String encryptionAlgorithm, String keyTransportEncryptionAlgorithm) {
        DataEncryptionParameters encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(encryptionAlgorithm);

        KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
        kekParams.setEncryptionCredential(credential);
        kekParams.setAlgorithm(keyTransportEncryptionAlgorithm);

        Encrypter encrypter = new Encrypter(encParams, kekParams);
        encrypter.setKeyPlacement(Encrypter.KeyPlacement.PEER);

        return encrypter;
    }

    public EncryptedAssertion buildWithEncrypterCredential(Encrypter encrypter) {
        Assertion assertion = buildUnencrypted();
        try {
            return encrypter.encrypt(assertion);
        } catch (EncryptionException e) {
            throw propagate(e);
        }
    }

    public AssertionBuilder withId(String id) {
        this.id = Optional.ofNullable(id);
        return this;
    }

    public AssertionBuilder withSubject(Subject subject) {
        this.subject = Optional.ofNullable(subject);
        return this;
    }

    public AssertionBuilder withIssuer(Issuer issuer) {
        this.issuer = Optional.ofNullable(issuer);
        return this;
    }

    public AssertionBuilder addAttributeStatement(AttributeStatement attributeStatement) {
        this.attributeStatements.add(attributeStatement);
        return this;
    }

    public AssertionBuilder withoutAttributeStatements() {
        attributeStatements.clear();
        return this;
    }

    public AssertionBuilder addAuthnStatement(AuthnStatement authnStatement) {
        authnStatements.add(authnStatement);
        return this;
    }

    public AssertionBuilder withoutAuthnStatements() {
        authnStatements.clear();
        return this;
    }

    public AssertionBuilder withoutSigning() {
        shouldSign = false;
        return this;
    }

    public AssertionBuilder withSignature(Signature signature) {
        this.signature = Optional.ofNullable(signature);
        return this;
    }

    public AssertionBuilder withIssueInstant(DateTime issueInstant) {
        this.issueInstant = Optional.ofNullable(issueInstant);
        return this;
    }

    public AssertionBuilder withVersion(SAMLVersion version) {
        this.version = version;
        return this;
    }

    public AssertionBuilder withConditions(Conditions conditions) {
        this.conditions = Optional.ofNullable(conditions);
        return this;
    }
}
