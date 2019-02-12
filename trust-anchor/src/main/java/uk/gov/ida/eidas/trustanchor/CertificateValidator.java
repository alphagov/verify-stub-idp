package uk.gov.ida.eidas.trustanchor;

import com.nimbusds.jose.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CertificateValidator {

    private final Base64X509CertificateDecoder decoder;

    CertificateValidator(Base64X509CertificateDecoder decoder){
            this.decoder = decoder;
    }

    Collection<String> checkCertificateValidity(List<Base64> x509CertChain, PublicKey publicKey) {
        List<String> errors = new ArrayList<>();

        X509Certificate x509Certificate;
        try {
            x509Certificate = decoder.decodeX509(x509CertChain.get(0));
        } catch (CertificateException e) {
            errors.add(String.format("Unable to decode x509 Certificate: %s", e.getMessage()));
            return errors;
        }

        if (!x509Certificate.getPublicKey().equals(publicKey)) {
            errors.add("X.509 Certificate does not match the public key");
        }

        errors.addAll(validateCertChain(x509Certificate, x509CertChain));

        return errors;
    }

    private List<String> validateCertChain(X509Certificate x509Certificate, List<Base64> x509CertChain) {
        X509Certificate signedCert = x509Certificate;

        List<String> chainErrors = new ArrayList<>(validateCert(x509Certificate));

        List<Base64> base64s = x509CertChain.subList(1, x509CertChain.size());
        for (Base64 base64cert : base64s) {
            X509Certificate signingCert;
            try {
                signingCert = decoder.decodeX509(base64cert);
            } catch (CertificateException e) {
                chainErrors.add(String.format("Unable to decode certificate %s: %s", base64cert, e.getMessage()));
                return chainErrors;
            }

            chainErrors.addAll(validateCert(signingCert));

            List<String> signatureErrors = verifySignature(signedCert, signingCert);
            chainErrors.addAll(signatureErrors);

            signedCert = signingCert;
        }

        return chainErrors;
    }

    private List<String> validateCert(X509Certificate signingCert) {
        List<String> certErrors = new ArrayList<>();
        try {
            signingCert.checkValidity();
        } catch (CertificateExpiredException e) {
            certErrors.add(String.format("X.509 certificate has expired (%s): %s",
                    signingCert.getSubjectX500Principal(), e.getMessage())
            );
        } catch (CertificateNotYetValidException e) {
            certErrors.add(String.format("Certificate %s is not yet valid: %s",
                    signingCert.getSubjectX500Principal(), e.getMessage())
            );
        }

        return certErrors;
    }

    private List<String> verifySignature(X509Certificate signedCert, X509Certificate signingCert) {
        List<String> signatureErrors = new ArrayList<>();
        try {
            signedCert.verify(signingCert.getPublicKey());
        } catch (CertificateException e) {
            signatureErrors.add(String.format("Unable to ensure that cert %s is signed by cert %s : %s",
                    signedCert.getSubjectX500Principal(),
                    signingCert.getSubjectX500Principal(),
                    e.getMessage())
            );
        } catch (NoSuchAlgorithmException e) {
            signatureErrors.add(String.format("Could not find algorithm %s", e.getMessage()));
        } catch (InvalidKeyException e) {
            signatureErrors.add(String.format("Cert %s is not signed by parent in chain cert %s : %s",
                    signedCert.getSubjectX500Principal(),
                    signingCert.getSubjectX500Principal(),
                    e.getMessage())
            );
        } catch (SignatureException e) {
            signatureErrors.add(String.format("Invalid signature for cert %s: %s",
                    signedCert.getSubjectX500Principal(),
                    e.getMessage())
            );
        } catch (NoSuchProviderException e) {
            signatureErrors.add(String.format("Unable to validate cert chain as unable to locate security provider: %s",
                    e.getMessage())
            );
        }
        return signatureErrors;
    }
}
