package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;
import static java.util.Optional.ofNullable;

public class AttributeQueryBuilder {

    private final static XMLObjectBuilderFactory factory = XMLObjectProviderRegistrySupport.getBuilderFactory();

    private boolean shouldSign = true;
    private List<Attribute> attributes = new ArrayList<>();

    private Optional<String> id = ofNullable("anId");
    private Optional<Subject> subject = ofNullable(SubjectBuilder.aSubject().build());
    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<Signature> signature = ofNullable(SignatureBuilder.aSignature().build());
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());

    public static AttributeQueryBuilder anAttributeQuery() {
        return new AttributeQueryBuilder();
    }

    public AttributeQuery build() {
        AttributeQuery attributeQuery = (AttributeQuery) factory
            .getBuilder(AttributeQuery.DEFAULT_ELEMENT_NAME)
            .buildObject(AttributeQuery.DEFAULT_ELEMENT_NAME, AttributeQuery.TYPE_NAME);

        subject.ifPresent(attributeQuery::setSubject);
        issuer.ifPresent(attributeQuery::setIssuer);
        issueInstant.ifPresent(attributeQuery::setIssueInstant);
        id.ifPresent(attributeQuery::setID);

        if (signature.isPresent()) {
            attributeQuery.setSignature(signature.get());
            try {
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(attributeQuery).marshall(attributeQuery);
                if (shouldSign) {
                    Signer.signObject(attributeQuery.getSignature());
                }
            } catch (MarshallingException | SignatureException e) {
                throw propagate(e);
            }
        }

        attributeQuery.getAttributes().addAll(attributes);

        return attributeQuery;
    }

    public AttributeQueryBuilder withSubject(Subject subject) {
        this.subject = ofNullable(subject);
        return this;
    }

    public AttributeQueryBuilder withoutSigning() {
        shouldSign = false;
        return this;
    }

    public AttributeQueryBuilder withId(String id) {
        this.id = ofNullable(id);
        return this;
    }

    public AttributeQueryBuilder withIssuer(Issuer issuer) {
        this.issuer = ofNullable(issuer);
        return this;
    }

    public AttributeQueryBuilder withIssueInstant(DateTime issueInstant) {
        this.issueInstant = ofNullable(issueInstant);
        return this;
    }

    public AttributeQueryBuilder withAttributes(List<Attribute> attributes){
        this.attributes = attributes;
        return this;
    }

    public AttributeQueryBuilder withSignature(Signature signature) {
        this.signature = ofNullable(signature);
        return this;
    }
}
