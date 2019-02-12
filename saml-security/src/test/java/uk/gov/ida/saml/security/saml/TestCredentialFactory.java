package uk.gov.ida.saml.security.saml;

import org.apache.commons.codec.binary.Base64;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.MessageFormat;

import static com.google.common.base.Throwables.propagate;

public class TestCredentialFactory {

    private final String publicCert;
    private final String privateCert;

    public TestCredentialFactory(String publicCert, String privateKey) {
        this.publicCert = publicCert;
        this.privateCert = privateKey;
    }

    public Credential getSigningCredential() {

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateCert));

        PrivateKey privateKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw propagate(e);
        }
        BasicCredential credential = new BasicCredential(getPublicKey(), privateKey);

        credential.setUsageType(UsageType.SIGNING);
        return credential;
    }

    public Credential getEncryptingCredential() {
        BasicCredential credential = new BasicCredential(getPublicKey());

        credential.setUsageType(UsageType.ENCRYPTION);
        return credential;
    }

    private PublicKey getPublicKey() {
        PublicKey publicKey;
        try {
            publicKey = createPublicKey(publicCert);
        } catch (CertificateException | UnsupportedEncodingException e) {
            throw propagate(e);
        }
        return publicKey;
    }

    public static PublicKey createPublicKey(String partialCert) throws CertificateException, UnsupportedEncodingException {
        CertificateFactory certificateFactory;
        certificateFactory = CertificateFactory.getInstance("X.509");
        String fullCert;
        if (partialCert.contains("-----BEGIN CERTIFICATE-----")) {
            fullCert = partialCert;
        } else {
            fullCert = MessageFormat.format("-----BEGIN CERTIFICATE-----\n{0}\n-----END CERTIFICATE-----", partialCert.trim());
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fullCert.getBytes("UTF-8"));
        Certificate certificate = certificateFactory.generateCertificate(byteArrayInputStream);
        return certificate.getPublicKey();
    }
}