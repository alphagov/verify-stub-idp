package uk.gov.ida.saml.security;

import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;



// this class exists to get an injectable instance with includeKeyInfo set to true
// it appears that SignatureWithKeyInfoFactory does something similar in a different way
// -> that is used in verify-saml-utils/CoreTransformersFactory which is used in lots of
// places
public class SignatureKeyInfoFactory extends SignatureFactory {


    public SignatureKeyInfoFactory(IdaKeyStoreCredentialRetriever keyStoreCredentialRetriever, SignatureAlgorithm signatureAlgorithm, DigestAlgorithm digestAlgorithm) {
        super(true, keyStoreCredentialRetriever, signatureAlgorithm, digestAlgorithm);
    }

}
