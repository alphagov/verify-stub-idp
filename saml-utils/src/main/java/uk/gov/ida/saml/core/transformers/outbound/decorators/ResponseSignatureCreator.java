package uk.gov.ida.saml.core.transformers.outbound.decorators;

import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.signature.Signature;
import uk.gov.ida.saml.security.SignatureFactory;

public class ResponseSignatureCreator {

    private final SignatureFactory signatureFactory;

    public ResponseSignatureCreator(SignatureFactory signatureFactory) {
        this.signatureFactory = signatureFactory;
    }

    public Response addUnsignedSignatureTo(Response input) {
        Signature signature = signatureFactory.createSignature(input.getSignatureReferenceID());
        input.setSignature(signature);
        return input;
    }
}
