package uk.gov.ida.saml.metadata;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class EidasTrustAnchorResolver {

    private final URI trustAnchorUri;
    private final Client client;
    private final KeyStore trustStore;

    public EidasTrustAnchorResolver(URI trustAnchorUri, Client client, KeyStore trustStore) {
        this.trustAnchorUri = trustAnchorUri;
        this.client = client;
        this.trustStore = trustStore;
    }

    public List<JWK> getTrustAnchors() throws ParseException, CertificateException, JOSEException, SignatureException {
        Response response = client.target(trustAnchorUri).request().get();
        String encodedJwsObject = response.readEntity(String.class);
        JWSObject trustAnchorMetadata = JWSObject.parse(encodedJwsObject);
        validateSignature(trustAnchorMetadata);
        return JWKSet.parse(trustAnchorMetadata.getPayload().toJSONObject()).getKeys();
    }

    private void validateSignature(JWSObject jwsObject) throws JOSEException, SignatureException, CertificateException {
        X509Certificate certificate = getCertificate(jwsObject);

        JWSVerifier jwsVerifier = new RSASSAVerifier(RSAKey.parse(certificate));
        boolean isValid = jwsObject.verify(jwsVerifier);
        if (!isValid){
            throw new SignatureException("Trust anchor metadata not signed with expected key. Certificate provided: " + certificate.getSubjectX500Principal().getName());
        }
    }

    private X509Certificate getCertificate(JWSObject jwsObject) throws CertificateException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        InputStream certStream = new ByteArrayInputStream(jwsObject.getHeader().getX509CertChain().get(0).decode());
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(certStream);

        try {
            List<X509Certificate> certificateList = Arrays.asList(certificate);
            CertPath cp = certificateFactory.generateCertPath(certificateList);
            PKIXParameters params = new PKIXParameters(trustStore);
            params.setRevocationEnabled(false);
            CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
            certPathValidator.validate(cp, params);
        } catch (GeneralSecurityException e) {
            throw new CertificateException("Error validating provided certificate - " + e.getMessage(), e);
        }

        return certificate;
    }
}
