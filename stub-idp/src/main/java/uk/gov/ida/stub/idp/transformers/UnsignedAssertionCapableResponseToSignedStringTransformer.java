package uk.gov.ida.stub.idp.transformers;

import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xml.signature.P;
import uk.gov.ida.saml.core.transformers.outbound.ResponseToSignedStringTransformer;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseAssertionSigner;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseSignatureCreator;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlResponseAssertionEncrypter;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlSignatureSigner;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;

import javax.inject.Inject;
import java.util.function.Function;

/**
 * Very similar to ResponseToSignedStringTransformer, but does not form a part of the saml-libs:
 * The capability to generate SAML with unsigned assertions is <u>only</u> required by the stub-idp.
 */
public class UnsignedAssertionCapableResponseToSignedStringTransformer implements Function<Response, String> {

    private final XmlObjectToBase64EncodedStringTransformer<?> xmlObjectToBase64EncodedStringTransformer;
    private final SamlSignatureSigner<Response> samlSignatureSigner;
    private final SamlResponseAssertionEncrypter samlResponseAssertionEncrypter;
    private final ResponseAssertionSigner responseAssertionSigner;
    private final ResponseSignatureCreator responseSignatureCreator;
    private final boolean signAssertions;

    @Inject
    public UnsignedAssertionCapableResponseToSignedStringTransformer(
        XmlObjectToBase64EncodedStringTransformer<?> xmlObjectToBase64EncodedStringTransformer,
        SamlSignatureSigner<Response> samlSignatureSigner,
        SamlResponseAssertionEncrypter samlResponseAssertionEncrypter,
        ResponseAssertionSigner responseAssertionSigner,
        ResponseSignatureCreator responseSignatureCreator,
        boolean signAssertions) {
        this.xmlObjectToBase64EncodedStringTransformer = xmlObjectToBase64EncodedStringTransformer;
        this.samlSignatureSigner = samlSignatureSigner;
        this.samlResponseAssertionEncrypter = samlResponseAssertionEncrypter;
        this.responseAssertionSigner = responseAssertionSigner;
        this.responseSignatureCreator = responseSignatureCreator;
        this.signAssertions = signAssertions;
    }

    @Override
    public String apply(final Response response) {
        final Response responseWithSignature = responseSignatureCreator.addUnsignedSignatureTo(response);
        if (signAssertions) {
            final Response assertionSignedResponse = responseAssertionSigner.signAssertions(responseWithSignature);
            final Response encryptedAssertionResponse = samlResponseAssertionEncrypter.encryptAssertions(assertionSignedResponse);
            final Response signedResponse = samlSignatureSigner.sign(encryptedAssertionResponse);
            return xmlObjectToBase64EncodedStringTransformer.apply(signedResponse);
        } else {
            final Response encryptedAssertionResponse = samlResponseAssertionEncrypter.encryptAssertions(responseWithSignature);
            final Response signedResponse = samlSignatureSigner.sign(encryptedAssertionResponse);
            return xmlObjectToBase64EncodedStringTransformer.apply(signedResponse);
        }
    }
}
