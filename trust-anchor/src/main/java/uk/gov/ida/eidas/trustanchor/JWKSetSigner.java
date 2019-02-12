package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

class JWKSetSigner {

  private final Base64URL thumbprint;
  private final PrivateKey privateKey;
  private final X509Certificate publicCert;

  public JWKSetSigner(PrivateKey privateKey, Base64URL thumbprint, X509Certificate publicCert) {
    this.privateKey = privateKey;
    this.thumbprint = thumbprint;
    this.publicCert = publicCert;
  }

  public JWSObject sign(JWKSet tokenSet) throws JOSEException, CertificateEncodingException {
    final JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                  .x509CertThumbprint(thumbprint)
                  .x509CertChain(Arrays.asList(Base64.encode(publicCert.getEncoded())))
                  .build();

    final JWSObject jwsObject = new JWSObject(header, new Payload(tokenSet.toJSONObject()));

    final JWSSigner signer = new RSASSASigner(privateKey);
    jwsObject.sign(signer);

    return jwsObject;
  }
}
