package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.xmlsec.signature.X509Certificate;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestEntityIds;

import java.util.Optional;

public class X509CertificateBuilder {

    private Optional<String> cert = Optional.empty();
    private Optional<String> certForEntityId = Optional.ofNullable(TestEntityIds.HUB_ENTITY_ID);

    public static X509CertificateBuilder aX509Certificate() {
        return new X509CertificateBuilder();
    }

    public X509Certificate build() {
        X509Certificate x509Certificate = new org.opensaml.xmlsec.signature.impl.X509CertificateBuilder().buildObject();

        if (cert.isPresent()){
            x509Certificate.setValue(cert.get());
        }
        else if (certForEntityId.isPresent()){
            x509Certificate.setValue(getCertForEntity());
        }
        return x509Certificate;
    }

    private String getCertForEntity() {
        return TestCertificateStrings.PUBLIC_SIGNING_CERTS.get(certForEntityId.get());
    }

    public X509CertificateBuilder withCert(String cert) {
        this.cert = Optional.ofNullable(cert);
        return this;
    }

    public X509CertificateBuilder withCertForEntityId(String entityId) {
        this.certForEntityId = Optional.ofNullable(entityId);
        return this;
    }
}
