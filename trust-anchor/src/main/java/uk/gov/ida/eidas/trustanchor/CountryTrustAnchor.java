package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;

import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CountryTrustAnchor {

    public static JWK make(List<X509Certificate> certificates, String keyId) {
        return make(certificates, keyId, true);
    }

//    This should only be used with `validateKey = false` to generate trust anchors for testing, never for production.
    public static JWK make(List<X509Certificate> certificates, String keyId, Boolean validateKey) {

        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("Certificate list empty");
        }

        final List<X509Certificate> sortedCertificates = CertificateSorter.sort(certificates);
        List<Base64> encodedSortedCertChain = sortedCertificates.stream()
                .map(certificate -> {
                    try {
                        return Base64.encode(certificate.getEncoded());
                    } catch (CertificateEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());


        JWK key = buildJWK(getSupportedKeyType(certificates), keyId, sortedCertificates.get(0), encodedSortedCertChain);

        if (validateKey) {
            Collection<String> errors = CountryTrustAnchorValidator.build().findErrors(key);
            if (!errors.isEmpty()) {
                throw new IllegalArgumentException(String.format("Managed to generate an invalid anchor: %s", String.join(", ", errors)));
            }
        }

        return key;
    }

    public static JWK parse(String json) throws ParseException {
        JWK key = JWK.parse(json);

        Collection<String> errors = findTrustAnchorErrors(key);

        if (!errors.isEmpty()) {
            throw new ParseException(String.format("JWK was not a valid trust anchor: %s", String.join(", ", errors)), 0);
        }

        return key;
    }

    /**
     * Deprecated - use {@link CountryTrustAnchorValidator} instead
     */
    @Deprecated
    public static Collection<String> findErrors(JWK trustAnchor) {
        return findTrustAnchorErrors(trustAnchor);
    }

    private static Collection<String> findTrustAnchorErrors(JWK trustAnchor) {
        if (trustAnchor instanceof RSAKey || trustAnchor instanceof ECKey) {
            return CountryTrustAnchorValidator.build().findErrors(trustAnchor);
        }

        return Collections.singletonList(String.format(
                "Unsupported key type %s. Expecting key type to be %s or %s",
                KeyType.RSA, KeyType.EC, trustAnchor.getKeyType()));
    }

    private static KeyType getSupportedKeyType(List<X509Certificate> certificates) {
        final List<PublicKey> publicKeys = certificates.stream().map(X509Certificate::getPublicKey).collect(Collectors.toList());

        if (publicKeys.stream().map(PublicKey::getClass).distinct().count() > 1) {
            throw new IllegalArgumentException(String.format(
                    "Certificate public key(s) in wrong format, got %s, expecting all %s or all %s",
                    publicKeys.stream().map(key -> key.getClass().getName()).collect(Collectors.joining(" ")),
                    RSAPublicKey.class.getName(), ECPublicKey.class.getName()));
        }

        if (publicKeys.stream().allMatch(key -> key instanceof RSAPublicKey)) {
            return KeyType.RSA;
        }

        if (publicKeys.stream().allMatch(key -> key instanceof ECPublicKey)) {
            return KeyType.EC;
        }

        throw new IllegalArgumentException(String.format(
                "Unsupported key type %s. Expecting key type to be %s or %s",
                publicKeys.get(0).getClass().getName(), KeyType.RSA, KeyType.EC));
    }

    private static JWK buildJWK(KeyType keyType, String keyId, X509Certificate certificate, List<Base64> encodedSortedCertChain) {
        if (keyType == KeyType.RSA) {
            return buildJWKFromRSAKey(keyId, certificate, encodedSortedCertChain);
        }

        if (keyType == KeyType.EC) {
            return buildJWKFromECKey(keyId, certificate, encodedSortedCertChain);
        }

        throw new IllegalArgumentException(String.format(
                "Could not build JWK from unsupported key type %s. Expecting key type to be %s or %s",
                keyType, KeyType.RSA, KeyType.EC));
    }

    private static JWK buildJWKFromRSAKey(String keyId, X509Certificate certificate, List<Base64> encodedSortedCertChain) {
        return new RSAKey.Builder((RSAPublicKey) certificate.getPublicKey())
                .algorithm(JWSAlgorithm.RS256)
                .keyOperations(Collections.singleton(KeyOperation.VERIFY))
                .keyID(keyId)
                .x509CertChain(encodedSortedCertChain)
                .build();
    }

    private static JWK buildJWKFromECKey(String keyId, X509Certificate certificate, List<Base64> encodedSortedCertChain) {
        ECPublicKey key = (ECPublicKey) certificate.getPublicKey();
        return new ECKey.Builder(ECKeyHelper.getCurve(key), key)
                .algorithm(ECKeyHelper.getJWSAlgorithm(key))
                .keyOperations(Collections.singleton(KeyOperation.VERIFY))
                .keyID(keyId)
                .x509CertChain(encodedSortedCertChain)
                .build();
    }
}
