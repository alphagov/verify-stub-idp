package uk.gov.ida.saml.security.saml.builders;

import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.extensions.IPAddress;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;
import static java.util.Optional.ofNullable;
import static uk.gov.ida.saml.security.saml.builders.AttributeStatementBuilder.anAttributeStatement;

public class AssertionBuilder {

    private static TestSamlObjectFactory openSamlXmlObjectFactory = new TestSamlObjectFactory();

    private boolean shouldSign = true;
    private SAMLVersion version = SAMLVersion.VERSION_20;
    private List<AttributeStatement> attributeStatements = new ArrayList<>();
    private List<AuthnStatement> authnStatements = new ArrayList<>();

    private Optional<String> id = ofNullable("some-assertion-id");
    private Optional<Subject> subject = ofNullable(SubjectBuilder.aSubject().build());
    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<Signature> signature = ofNullable(SignatureBuilder.aSignature().build());
    private Optional<Conditions> conditions = ofNullable(ConditionsBuilder.aConditions().build());
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());

    public static AssertionBuilder anAssertion() {
        return new AssertionBuilder();
    }

    public static Assertion anAuthnStatementAssertion() {
        return anAssertion()
                .addAuthnStatement(AuthnStatementBuilder.anAuthnStatement().build())
                .addAttributeStatement(anAttributeStatement().addAttribute(buildAnIPAddress()).build())
                .build();
    }


    public Assertion build() {
        Assertion assertion = openSamlXmlObjectFactory.createAssertion();

        id.ifPresent(assertion::setID);

        assertion.setVersion(version);

        subject.ifPresent(assertion::setSubject);

        issueInstant.ifPresent(assertion::setIssueInstant);

        assertion.getAttributeStatements().addAll(attributeStatements);
        assertion.getAuthnStatements().addAll(authnStatements);

        issuer.ifPresent(assertion::setIssuer);
        conditions.ifPresent(assertion::setConditions);
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

    public AssertionBuilder withId(String id) {
        this.id = ofNullable(id);
        return this;
    }

    public AssertionBuilder withIssuer(Issuer issuer) {
        this.issuer = ofNullable(issuer);
        return this;
    }

    public AssertionBuilder addAttributeStatement(AttributeStatement attributeStatement) {
        this.attributeStatements.add(attributeStatement);
        return this;
    }

    public AssertionBuilder addAuthnStatement(AuthnStatement authnStatement) {
        authnStatements.add(authnStatement);
        return this;
    }

    public AssertionBuilder withoutSigning() {
        shouldSign = false;
        return this;
    }

    public AssertionBuilder withSignature(Signature signature) {
        this.signature = ofNullable(signature);
        return this;
    }

    private static Attribute buildAnIPAddress() {

        Attribute ipAddressAttribute = openSamlXmlObjectFactory.createAttribute();
        ipAddressAttribute.setFriendlyName("IPAddress");
        ipAddressAttribute.setName("TXN_IPaddress");

        IPAddress ipAddressAttributeValue = openSamlXmlObjectFactory.createIPAddressAttributeValue("1.2.3.4");

        ipAddressAttribute.getAttributeValues().add(ipAddressAttributeValue);

        return ipAddressAttribute;
    }

}
