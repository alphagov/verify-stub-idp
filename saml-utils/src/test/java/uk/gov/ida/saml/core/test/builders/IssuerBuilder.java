package uk.gov.ida.saml.core.test.builders;

import org.opensaml.saml.saml2.core.Issuer;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.test.TestCertificateStrings;


public class IssuerBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private String issuerId = TestCertificateStrings.TEST_ENTITY_ID;
    private String format = null;

    public static IssuerBuilder anIssuer() {
        return new IssuerBuilder();
    }

    public Issuer build() {

        Issuer issuer = openSamlXmlObjectFactory.createIssuer(issuerId);

        issuer.setFormat(format);

        return issuer;
    }

    public IssuerBuilder withIssuerId(String issuerId) {
        this.issuerId = issuerId;
        return this;
    }

    public IssuerBuilder withFormat(String format) {
        this.format = format;
        return this;
    }
}
