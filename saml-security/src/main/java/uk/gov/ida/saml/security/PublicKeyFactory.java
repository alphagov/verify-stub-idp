package uk.gov.ida.saml.security;

import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.opensaml.xmlsec.signature.X509Certificate;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static com.google.common.base.Throwables.propagate;

public class PublicKeyFactory {

    private final CertificateFactory certificateFactory;

    public PublicKeyFactory() throws CertificateException {
        certificateFactory = CertificateFactory.getInstance("X.509");
    }

    public PublicKey create(X509Certificate x509Certificate) {
        try {
            byte[] derValue = Base64.decode(x509Certificate.getValue());
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(derValue));
            return certificate.getPublicKey();
        } catch (Base64DecodingException | CertificateException e) {
            throw propagate(e);
        }
    }

}
