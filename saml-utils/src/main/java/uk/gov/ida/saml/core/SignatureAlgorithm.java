package uk.gov.ida.saml.core;

import org.opensaml.xmlsec.signature.support.SignatureConstants;

public enum SignatureAlgorithm {
    DSA_SHA1 {
        @Override
        public String toString() {
            return SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1;
        }
    },
    RSA_SHA1 {
        @Override
        public String toString() {
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
        }
    },
    RSA_SHA256 {
        @Override
        public String toString() {
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256;
        }
    },
    RSA_SHA512 {
        @Override
        public String toString() {
            return SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512;
        }
    };
}
