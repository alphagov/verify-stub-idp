package uk.gov.ida.stub.idp.domain.factories;

import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseAssertionSigner;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseSignatureCreator;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlResponseAssertionEncrypter;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlSignatureSigner;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.KeyStoreBackedEncryptionCredentialResolver;
import uk.gov.ida.saml.security.SignatureFactory;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;
import uk.gov.ida.stub.idp.transformers.StubIdpResponseToSignedStringTransformer;

/**
 * Replaces <i>some</i> calls that would have been made to CoreTransformersFactory in saml-libs - in particular,
 * provides a ResponseStringTransformer that can optionally skip signing of assertions: essential for stub countries
 * (but not much else).
 */
public class StubCoreTransformersFactory {

    public static StubIdpResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore publicKeyStore,
        final IdaKeyStore keyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final SignatureAlgorithm signatureAlgorithm,
        final DigestAlgorithm digestAlgorithm,
        final EncrypterFactory encrypterFactory,
        final boolean signAssertions) {

        SignatureFactory signatureFactory = new SignatureFactory(
            new IdaKeyStoreCredentialRetriever(keyStore),
            signatureAlgorithm,
            digestAlgorithm);

        ResponseAssertionSigner responseAssertionSigner = new ResponseAssertionSigner(signatureFactory);

        SamlResponseAssertionEncrypter responseAssertionEncrypter =
            new SamlResponseAssertionEncrypter(
                new KeyStoreBackedEncryptionCredentialResolver(publicKeyStore),
                encrypterFactory,
                entityToEncryptForLocator);

        return new StubIdpResponseToSignedStringTransformer(
            new XmlObjectToBase64EncodedStringTransformer<>(),
            new SamlSignatureSigner<>(),
            responseAssertionEncrypter,
            responseAssertionSigner,
            new ResponseSignatureCreator(signatureFactory),
            signAssertions
        );
    }


}
