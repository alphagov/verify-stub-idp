package uk.gov.ida.common.shared.security;

import javax.inject.Inject;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class PublicKeyFactory {

    private X509CertificateFactory certificateFactory;

    @Inject
    public PublicKeyFactory(X509CertificateFactory certificateFactory) {
        this.certificateFactory = certificateFactory;
    }

    public PublicKey createPublicKey(String partialCert) {
        Certificate certificate = certificateFactory.createCertificate(partialCert);
        return certificate.getPublicKey();
    }
}
