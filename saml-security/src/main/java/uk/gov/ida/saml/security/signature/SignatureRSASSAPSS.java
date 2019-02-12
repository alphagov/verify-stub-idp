package uk.gov.ida.saml.security.signature;

import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;

import javax.annotation.Nonnull;

public final class SignatureRSASSAPSS implements SignatureAlgorithm {

    /** {@inheritDoc} */
    @Nonnull public String getKey() {
        return JCAConstants.KEY_ALGO_RSA;
    }

    /** {@inheritDoc} */
    @Nonnull public String getURI() {
        return XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1;
    }

    /** {@inheritDoc} */
    @Nonnull public AlgorithmType getType() {
        return AlgorithmType.Signature;
    }

    /** {@inheritDoc} */
    @Nonnull public String getJCAAlgorithmID() {
        return "RSAwithSHA256andMGF1";
    }

    /** {@inheritDoc} */
    @Nonnull public String getDigest() {
        return JCAConstants.DIGEST_SHA256;
    }

}
