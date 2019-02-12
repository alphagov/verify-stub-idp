package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SAMLRuntimeException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.security.SignatureFactory;

import javax.inject.Inject;

public class ResponseAssertionSigner {

    private final SignatureFactory signatureFactory;

    @Inject
    public ResponseAssertionSigner(SignatureFactory signatureFactory) {
        this.signatureFactory = signatureFactory;
    }

    public Response signAssertions(Response response) {
        for (Assertion assertion : response.getAssertions()) {
            if (assertion.getSignature() == null) {
                Signature signature = signatureFactory.createSignature(assertion.getSignatureReferenceID());
                assertion.setSignature(signature);

                try {
                    XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
                    Signer.signObject(assertion.getSignature());
                } catch (SignatureException | MarshallingException e) {
                    throw new SAMLRuntimeException("Problem signing assertion " + assertion, e);
                }
            }
        }
        return response;
    }
}
