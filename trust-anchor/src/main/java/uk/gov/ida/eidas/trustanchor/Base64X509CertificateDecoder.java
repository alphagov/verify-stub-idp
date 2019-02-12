package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Base64X509CertificateDecoder {

    private final CertificateFactory certificateFactory;

    public Base64X509CertificateDecoder() throws CertificateException {
        certificateFactory = CertificateFactory.getInstance("X.509");
    }

    X509Certificate decodeX509(Base64 base64) throws CertificateException {
        InputStream certStream = new ByteArrayInputStream(base64.decode());
        return (X509Certificate) certificateFactory.generateCertificate(certStream);
    }
}
