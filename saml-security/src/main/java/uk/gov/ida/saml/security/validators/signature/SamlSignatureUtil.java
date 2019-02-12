package uk.gov.ida.saml.security.validators.signature;

import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.saml.common.SAMLRuntimeException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.SignatureImpl;

public abstract class SamlSignatureUtil {

    public static boolean isSignaturePresent(Signature signature) {
        SignatureImpl signatureImpl = (SignatureImpl) signature;
        if (signatureImpl.getXMLSignature() == null) {
            throw new SAMLRuntimeException("Signature has not been marshalled");
        }
        try {
            return signatureImpl.getXMLSignature().getSignatureValue().length != 0;
        } catch (XMLSignatureException e) {
            throw new SAMLRuntimeException("Unknown problem trying to to check signature", e);
        }
    }
}
