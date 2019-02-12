package uk.gov.ida.saml.metadata.domain;

import java.security.cert.X509Certificate;
import java.util.List;

public class MetadataSigningTrustedCertificates {
    private List<X509Certificate> certificates;

    public MetadataSigningTrustedCertificates(List<X509Certificate> certificates) {
        this.certificates = certificates;
    }

    public List<X509Certificate> getCertificates() {
        return certificates;
    }
}
