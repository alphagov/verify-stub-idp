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
import uk.gov.ida.saml.security.SignatureWithKeyInfoFactory;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;
import uk.gov.ida.stub.idp.transformers.UnsignedAssertionCapableResponseToSignedStringTransformer;

/**
 * Replaces <i>some</i> calls that would have been made to CoreTransformersFactory in saml-libs - in particular,
 * provides a ResponseStringTransformer that can optionally skip signing of assertions: essential for stub countries
 * (but not much else).
 */
public class StubCoreTransformersFactory {

    static UnsignedAssertionCapableResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore publicKeyStore,
        final IdaKeyStore keyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final SignatureAlgorithm signatureAlgorithm,
        final DigestAlgorithm digestAlgorithm,
        final boolean signAssertions) {
        return getResponseStringTransformer(publicKeyStore, keyStore, entityToEncryptForLocator, signatureAlgorithm,
            digestAlgorithm, new EncrypterFactory(), signAssertions);
    }

    public static UnsignedAssertionCapableResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore publicKeyStore,
        final IdaKeyStore keyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final SignatureAlgorithm signatureAlgorithm,
        final DigestAlgorithm digestAlgorithm,
        final EncrypterFactory encrypterFactory,
        final boolean signAssertions) {
        SignatureFactory signatureFactory = new SignatureFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
        ResponseAssertionSigner responseAssertionSigner = new ResponseAssertionSigner(signatureFactory);
        return getResponseStringTransformer(publicKeyStore, entityToEncryptForLocator, encrypterFactory, signatureFactory, responseAssertionSigner, signAssertions);
    }

    static UnsignedAssertionCapableResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore encryptionKeyStore,
        final IdaKeyStore keyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final String publicSigningKey,
        final String issuerId,
        final SignatureAlgorithm signatureAlgorithm,
        final DigestAlgorithm digestAlgorithm,
        final boolean signAssertions
    ) {
        SignatureFactory signatureFactory = new SignatureWithKeyInfoFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm, issuerId, publicSigningKey);
        ResponseAssertionSigner responseAssertionSigner = new ResponseAssertionSigner(signatureFactory);
        return getResponseStringTransformer(encryptionKeyStore, entityToEncryptForLocator, new EncrypterFactory(), signatureFactory, responseAssertionSigner, signAssertions);
    }

    static UnsignedAssertionCapableResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore publicKeyStore,
        final IdaKeyStore keyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final ResponseAssertionSigner responseAssertionSigner,
        final SignatureAlgorithm signatureAlgorithm,
        final DigestAlgorithm digestAlgorithm,
        final boolean signAssertions) {
        SignatureFactory signatureFactory = new SignatureFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
        return getResponseStringTransformer(publicKeyStore, entityToEncryptForLocator, new EncrypterFactory(), signatureFactory, responseAssertionSigner, signAssertions);
    }

    private static UnsignedAssertionCapableResponseToSignedStringTransformer getResponseStringTransformer(
        final EncryptionKeyStore publicKeyStore,
        final EntityToEncryptForLocator entityToEncryptForLocator,
        final EncrypterFactory encrypterFactory,
        final SignatureFactory signatureFactory,
        final ResponseAssertionSigner responseAssertionSigner,
        final boolean signAssertions) {
        SamlResponseAssertionEncrypter responseAssertionEncrypter =
            new SamlResponseAssertionEncrypter(
                new KeyStoreBackedEncryptionCredentialResolver(publicKeyStore),
                encrypterFactory,
                entityToEncryptForLocator);
        return new UnsignedAssertionCapableResponseToSignedStringTransformer(
            new XmlObjectToBase64EncodedStringTransformer<>(),
            new SamlSignatureSigner<>(),
            responseAssertionEncrypter,
            responseAssertionSigner,
            new ResponseSignatureCreator(signatureFactory),
            signAssertions
        );
    }


}
