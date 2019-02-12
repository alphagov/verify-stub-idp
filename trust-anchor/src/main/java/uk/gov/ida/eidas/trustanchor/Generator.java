package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Generator {
  private final JWKSetSigner signer;

  public Generator(PrivateKey signingKey, X509Certificate certificate) {
    this.signer = new JWKSetSigner(signingKey, null, certificate);
  }

  public JWSObject generateFromMap(Map<String, List<X509Certificate>> trustAnchorMap) throws JOSEException, CertificateEncodingException {
    List<JWK> keys = new ArrayList<>();
    for (Map.Entry<String, List<X509Certificate>> entry : trustAnchorMap.entrySet()) {
      keys.add(CountryTrustAnchor.make(entry.getValue(), entry.getKey()));
    }

    return signer.sign(new JWKSet(keys));
  }

  public JWSObject generate(List<String> inputJSONs) throws JOSEException, ParseException, CertificateEncodingException {
    List<JWK> keys = new ArrayList<>();
    for (String input : inputJSONs) {
      keys.add(CountryTrustAnchor.parse(input));
    }

    return signer.sign(new JWKSet(keys));
  }
}
