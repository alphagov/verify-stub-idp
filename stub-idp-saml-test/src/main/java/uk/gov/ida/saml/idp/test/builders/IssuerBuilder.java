package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.Issuer;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;

public class IssuerBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> issuerId = Optional.ofNullable(TestCertificateStrings.TEST_ENTITY_ID);
    private String format = null;

    public static IssuerBuilder anIssuer() {
        return new IssuerBuilder();
    }

    public Issuer build() {

        Issuer issuer = openSamlXmlObjectFactory.createIssuer(issuerId.orElse(null));

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
