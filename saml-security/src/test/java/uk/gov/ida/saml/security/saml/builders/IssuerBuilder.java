package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.saml.saml2.core.Issuer;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.Optional;

public class IssuerBuilder {

    private TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    private Optional<String> issuerId = Optional.of(TestCertificateStrings.TEST_ENTITY_ID);
    private String format = null;

    public static IssuerBuilder anIssuer() {
        return new IssuerBuilder();
    }

    public Issuer build() {

        Issuer issuer = testSamlObjectFactory.createIssuer(issuerId.orElse(null));

        issuer.setFormat(format);

        return issuer;
    }

    public IssuerBuilder withIssuerId(String issuerId) {
        this.issuerId = Optional.ofNullable(issuerId);
        return this;
    }


    public IssuerBuilder withFormat(String format) {
        this.format = format;
        return this;
    }
}
