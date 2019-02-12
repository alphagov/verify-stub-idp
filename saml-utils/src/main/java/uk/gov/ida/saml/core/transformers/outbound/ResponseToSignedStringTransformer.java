package uk.gov.ida.saml.core.transformers.outbound;

import org.opensaml.saml.saml2.core.Response;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseAssertionSigner;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseSignatureCreator;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlResponseAssertionEncrypter;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlSignatureSigner;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;

import javax.inject.Inject;
import java.util.function.Function;

public class ResponseToSignedStringTransformer implements Function<Response, String> {

    protected final XmlObjectToBase64EncodedStringTransformer<?> xmlObjectToBase64EncodedStringTransformer;
    protected final SamlSignatureSigner<Response> samlSignatureSigner;
    protected final SamlResponseAssertionEncrypter samlResponseAssertionEncrypter;
    protected final ResponseAssertionSigner responseAssertionSigner;
    protected final ResponseSignatureCreator responseSignatureCreator;

    @Inject
    public ResponseToSignedStringTransformer(
            XmlObjectToBase64EncodedStringTransformer<?> xmlObjectToBase64EncodedStringTransformer,
            SamlSignatureSigner<Response> samlSignatureSigner,
            SamlResponseAssertionEncrypter samlResponseAssertionEncrypter,
            ResponseAssertionSigner responseAssertionSigner,
            ResponseSignatureCreator responseSignatureCreator) {
        this.xmlObjectToBase64EncodedStringTransformer = xmlObjectToBase64EncodedStringTransformer;
        this.samlSignatureSigner = samlSignatureSigner;
        this.samlResponseAssertionEncrypter = samlResponseAssertionEncrypter;
        this.responseAssertionSigner = responseAssertionSigner;
        this.responseSignatureCreator = responseSignatureCreator;
    }

    @Override
    public String apply(final Response response) {
        final Response responseWithSignature = responseSignatureCreator.addUnsignedSignatureTo(response);
        final Response assertionSignedResponse = responseAssertionSigner.signAssertions(responseWithSignature);
        final Response encryptedAssertionResponse = samlResponseAssertionEncrypter.encryptAssertions(assertionSignedResponse);
        final Response signedResponse = samlSignatureSigner.sign(encryptedAssertionResponse);

        return xmlObjectToBase64EncodedStringTransformer.apply(signedResponse);
    }

}
