package uk.gov.ida.saml.core.transformers.outbound.decorators;


import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLRuntimeException;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;

public class SamlSignatureSigner<T extends SignableXMLObject> {

    public T sign(T input) {

        Signature rootObjectSignature = input.getSignature();
        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(input).marshall(input);
            Signer.signObject(rootObjectSignature);
        } catch (SignatureException | MarshallingException e) {
            throw new SAMLRuntimeException("Unknown problem while signing SAML object", e);
        }

        return input;
    }
}
