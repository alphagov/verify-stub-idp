package uk.gov.ida.common.shared.security;

import uk.gov.ida.common.shared.configuration.DeserializablePublicKeyConfiguration;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.ida.common.shared.security.Certificate.BEGIN_CERT;
import static uk.gov.ida.common.shared.security.Certificate.END_CERT;

public class CertificateStore {

    private final List<DeserializablePublicKeyConfiguration> publicEncryptionKeyConfigurations;
    private final List<DeserializablePublicKeyConfiguration> publicSigningKeyConfigurations;

    public CertificateStore(
            List<DeserializablePublicKeyConfiguration> publicEncryptionKeyConfigurations,
            List<DeserializablePublicKeyConfiguration> publicSigningKeyConfiguration) {

        this.publicEncryptionKeyConfigurations = publicEncryptionKeyConfigurations;
        this.publicSigningKeyConfigurations = publicSigningKeyConfiguration;
    }

    public List<Certificate> getEncryptionCertificates() {
        List<Certificate> certs = new ArrayList<>();
        for (DeserializablePublicKeyConfiguration certConfig : publicEncryptionKeyConfigurations) {
            certs.add(new Certificate(certConfig.getName(), stripHeaders(certConfig.getCert()), Certificate.KeyUse.Encryption));
        }
        return certs;
    }

    public List<Certificate> getSigningCertificates() {
        List<Certificate> certs = new ArrayList<>();
        for (DeserializablePublicKeyConfiguration certConfig : publicSigningKeyConfigurations) {
            certs.add(new Certificate(certConfig.getName(), stripHeaders(certConfig.getCert()), Certificate.KeyUse.Signing));
        }
        return certs;
    }

    private String stripHeaders(final String originalCertificate) {
        String strippedCertificate = originalCertificate;
        if (originalCertificate.contains(BEGIN_CERT)){
            strippedCertificate = originalCertificate.replace(BEGIN_CERT, "");
        }
        if (originalCertificate.contains(END_CERT)){
            strippedCertificate = strippedCertificate.replace(END_CERT, "");
        }
        return strippedCertificate.replace(" ","");
    }
}
