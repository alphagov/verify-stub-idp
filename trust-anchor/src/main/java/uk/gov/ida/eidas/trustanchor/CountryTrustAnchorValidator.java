package uk.gov.ida.eidas.trustanchor;

import com.google.common.collect.ImmutableList;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

public class CountryTrustAnchorValidator {

    private final CertificateValidator certificateValidator;

    /**
     * @throws IllegalStateException if cannot build x509 CertificateFactory
     */
    public static CountryTrustAnchorValidator build() {
        Base64X509CertificateDecoder decoder;
        try {
            decoder = new Base64X509CertificateDecoder();
        } catch (CertificateException e) {
            throw new IllegalStateException("Unable to build x509 Cert decoder", e);
        }
        return new CountryTrustAnchorValidator(new CertificateValidator(decoder));
    }

    CountryTrustAnchorValidator(CertificateValidator validator) {
        this.certificateValidator = validator;
    }

    public Collection<String> findErrors(JWK anchor) {
        Collection<String> errors = new HashSet<>();

        if (!isKeyOperationsVerify(anchor)) {
            errors.add(String.format("Expecting key operations to only contain %s", KeyOperation.VERIFY));
        }
        if (!isKeyIDPresent(anchor)) {
            errors.add("Expecting a KeyID");
        }

        errors.addAll(checkAlgorithmValid(anchor));

        if (hasCertificates(anchor)) {
            errors.addAll(validateCertificates(anchor));
        } else {
            errors.add("Expecting at least one X.509 certificate");
        }

        return errors;
    }

    @SuppressWarnings("ConstantConditions")
    private Collection<String> validateCertificates(JWK anchor) {
        PublicKey publicKey = null;
        try {

            if (isKeyTypeRSA(anchor)) {
                RSAKey rsaKey = (RSAKey) anchor;
                publicKey = rsaKey.toPublicKey();
            }

            if (isKeyTypeEC(anchor)) {
                ECKey ecKey = (ECKey) anchor;
                publicKey = ecKey.toPublicKey();
            }
        } catch (JOSEException e) {
            return ImmutableList.of(String.format("Error getting public key from trust anchor: %s", e.getMessage()));
        }

        return certificateValidator.checkCertificateValidity(anchor.getX509CertChain(), publicKey);
    }

    private Collection<String> checkAlgorithmValid(JWK anchor) {

        if (isKeyTypeRSA(anchor) && !isAlgorithmRS256((RSAKey) anchor)) {
            return Collections.singletonList(String.format("Expecting algorithm to be %s, was %s", JWSAlgorithm.RS256, anchor.getAlgorithm()));
        }

        if (isKeyTypeEC(anchor) && !isAlgorithmValidEC((ECKey) anchor)) {
            return Collections.singletonList(String.format("Algorithm %s is invalid for curve %s", anchor.getAlgorithm(), ((ECKey) anchor).getCurve().getName()));
        }

        return Collections.emptyList();
    }

    private boolean isKeyTypeRSA(JWK anchor) {
        return anchor instanceof RSAKey || KeyType.RSA.equals(anchor.getKeyType());
    }

    private boolean isKeyTypeEC(JWK anchor) {
        return anchor instanceof ECKey || KeyType.EC.equals(anchor.getKeyType());
    }

    private boolean isAlgorithmRS256(RSAKey anchor) {
        return Optional.ofNullable(anchor.getAlgorithm())
                .map(alg -> alg.equals(JWSAlgorithm.RS256))
                .orElse(false);
    }

    private boolean isAlgorithmValidEC(ECKey anchor) {
        return Optional.ofNullable(anchor.getAlgorithm())
                .map(alg -> alg.equals(ECKeyHelper.getJWSAlgorithm(anchor.getCurve())))
                .orElse(false);
    }

    private boolean isKeyOperationsVerify(JWK anchor) {
        return Optional.ofNullable(anchor.getKeyOperations())
                .filter(ops -> ops.size() == 1)
                .map(ops -> ops.contains(KeyOperation.VERIFY))
                .orElse(false);
    }

    private boolean isKeyIDPresent(JWK anchor) {
        return Optional.ofNullable(anchor.getKeyID())
                .map(kid -> !kid.isEmpty())
                .orElse(false);
    }

    private boolean hasCertificates(JWK anchor) {
        return Optional.ofNullable(anchor.getX509CertChain())
                .map(certChain -> certChain.size() > 0)
                .orElse(false);
    }
}
