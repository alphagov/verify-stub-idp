package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Throwables.propagate;
import static java.util.Optional.ofNullable;

public class AttributeQueryBuilder {

    private boolean shouldSign = true;
    private List<Attribute> attributes = new ArrayList<>();

    private Optional<String> id = ofNullable("anId");
    private Optional<Subject> subject = ofNullable(SubjectBuilder.aSubject().build());
    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<Signature> signature = ofNullable(SignatureBuilder.aSignature().build());

    public static AttributeQueryBuilder anAttributeQuery() {
        return new AttributeQueryBuilder();
    }

    public AttributeQuery build() {
        AttributeQuery attributeQuery = new TestSamlObjectFactory().createAttributeQuery();

        subject.ifPresent(attributeQuery::setSubject);

        issuer.ifPresent(attributeQuery::setIssuer);

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
}
