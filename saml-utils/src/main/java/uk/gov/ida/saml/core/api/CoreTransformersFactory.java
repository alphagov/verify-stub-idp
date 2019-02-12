package uk.gov.ida.saml.core.api;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.transformers.inbound.Cycle3DatasetFactory;
import uk.gov.ida.saml.core.transformers.inbound.HubAssertionUnmarshaller;
import uk.gov.ida.saml.core.transformers.outbound.ResponseToSignedStringTransformer;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseAssertionSigner;
import uk.gov.ida.saml.core.transformers.outbound.decorators.ResponseSignatureCreator;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlResponseAssertionEncrypter;
import uk.gov.ida.saml.core.transformers.outbound.decorators.SamlSignatureSigner;
import uk.gov.ida.saml.deserializers.ElementToOpenSamlXMLObjectTransformer;
import uk.gov.ida.saml.deserializers.OpenSamlXMLObjectUnmarshaller;
import uk.gov.ida.saml.deserializers.StringToOpenSamlObjectTransformer;
import uk.gov.ida.saml.deserializers.parser.SamlObjectParser;
import uk.gov.ida.saml.deserializers.validators.Base64StringDecoder;
import uk.gov.ida.saml.deserializers.validators.NotNullSamlStringValidator;
import uk.gov.ida.saml.deserializers.validators.SizeValidator;
import uk.gov.ida.saml.metadata.transformers.KeyDescriptorsUnmarshaller;
import uk.gov.ida.saml.security.CredentialFactorySignatureValidator;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.KeyStoreBackedEncryptionCredentialResolver;
import uk.gov.ida.saml.security.SamlMessageSignatureValidator;
import uk.gov.ida.saml.security.SignatureFactory;
import uk.gov.ida.saml.security.SignatureValidator;
import uk.gov.ida.saml.security.SignatureWithKeyInfoFactory;
import uk.gov.ida.saml.security.SigningCredentialFactory;
import uk.gov.ida.saml.security.SigningKeyStore;
import uk.gov.ida.saml.security.validators.signature.SamlRequestSignatureValidator;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;
import uk.gov.ida.saml.serializers.XmlObjectToElementTransformer;

public class CoreTransformersFactory {
    public KeyDescriptorsUnmarshaller getCertificatesToKeyDescriptorsTransformer() {
        return new KeyDescriptorsUnmarshaller(
                new OpenSamlXmlObjectFactory()
        );
    }

    public <T extends XMLObject> XmlObjectToElementTransformer<T> getXmlObjectToElementTransformer() {
        return new XmlObjectToElementTransformer<>();
    }

    public <T extends XMLObject> ElementToOpenSamlXMLObjectTransformer<T> getElementToOpenSamlXmlObjectTransformer() {
        return new ElementToOpenSamlXMLObjectTransformer<>(
                new SamlObjectParser()
        );
    }

    public HubAssertionUnmarshaller getAssertionToHubAssertionTransformer(String hubEntityId) {
        return new HubAssertionUnmarshaller(
                new Cycle3DatasetFactory(),
                hubEntityId
        );
    }

    public <TOutput extends XMLObject> StringToOpenSamlObjectTransformer<TOutput> getStringtoOpenSamlObjectTransformer(
            final SizeValidator sizeValidator
    ) {
        return new StringToOpenSamlObjectTransformer<>(
                new NotNullSamlStringValidator(),
                new Base64StringDecoder(),
                sizeValidator,
                new OpenSamlXMLObjectUnmarshaller<TOutput>(new SamlObjectParser())
        );
    }

    public <TInput extends RequestAbstractType> SamlRequestSignatureValidator<TInput> getSamlRequestSignatureValidator(
            final SigningKeyStore publicKeyStore
    ) {
        return new SamlRequestSignatureValidator<>(
                new SamlMessageSignatureValidator(getSignatureValidator(publicKeyStore))
        );
    }

    public SignatureValidator getSignatureValidator(SigningKeyStore signingKeyStore) {
        SigningCredentialFactory signingCredentialFactory = new SigningCredentialFactory(signingKeyStore);
        return getSignatureValidator(signingCredentialFactory);
    }

    public SignatureValidator getSignatureValidator(SigningCredentialFactory publicCredentialFactory) {
        return new CredentialFactorySignatureValidator(publicCredentialFactory);
    }

    public ResponseToSignedStringTransformer getResponseStringTransformer(
            final EncryptionKeyStore publicKeyStore,
            final IdaKeyStore keyStore,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final SignatureAlgorithm signatureAlgorithm,
            final DigestAlgorithm digestAlgorithm) {
        return getResponseStringTransformer(publicKeyStore, keyStore, entityToEncryptForLocator, signatureAlgorithm,
                digestAlgorithm, new EncrypterFactory());
    }

    public ResponseToSignedStringTransformer getResponseStringTransformer(
            final EncryptionKeyStore publicKeyStore,
            final IdaKeyStore keyStore,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final SignatureAlgorithm signatureAlgorithm,
            final DigestAlgorithm digestAlgorithm,
            final EncrypterFactory encrypterFactory) {
        SignatureFactory signatureFactory = new SignatureFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
        ResponseAssertionSigner responseAssertionSigner = new ResponseAssertionSigner(signatureFactory);
        return getResponseStringTransformer(publicKeyStore, entityToEncryptForLocator, encrypterFactory, signatureFactory, responseAssertionSigner);
    }

    public ResponseToSignedStringTransformer getResponseStringTransformer(
            final EncryptionKeyStore encryptionKeyStore,
            final IdaKeyStore keyStore,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final String publicSigningKey,
            final String issuerId,
            final SignatureAlgorithm signatureAlgorithm,
            final DigestAlgorithm digestAlgorithm
    ) {
        SignatureFactory signatureFactory = new SignatureWithKeyInfoFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm, issuerId, publicSigningKey);
        ResponseAssertionSigner responseAssertionSigner = new ResponseAssertionSigner(signatureFactory);
        return getResponseStringTransformer(encryptionKeyStore, entityToEncryptForLocator, new EncrypterFactory(), signatureFactory, responseAssertionSigner);
    }

    public ResponseToSignedStringTransformer getResponseStringTransformer(
            final EncryptionKeyStore publicKeyStore,
            final IdaKeyStore keyStore,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final ResponseAssertionSigner responseAssertionSigner,
            final SignatureAlgorithm signatureAlgorithm,
            final DigestAlgorithm digestAlgorithm) {
        SignatureFactory signatureFactory = new SignatureFactory(new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
        return getResponseStringTransformer(publicKeyStore, entityToEncryptForLocator, new EncrypterFactory(), signatureFactory, responseAssertionSigner);
    }

    private ResponseToSignedStringTransformer getResponseStringTransformer(
            final EncryptionKeyStore publicKeyStore,
            final EntityToEncryptForLocator entityToEncryptForLocator,
            final EncrypterFactory encrypterFactory,
            final SignatureFactory signatureFactory,
            final ResponseAssertionSigner responseAssertionSigner) {
        SamlResponseAssertionEncrypter responseAssertionEncrypter =
                new SamlResponseAssertionEncrypter(
                        new KeyStoreBackedEncryptionCredentialResolver(publicKeyStore),
                        encrypterFactory,
                        entityToEncryptForLocator);
        return new ResponseToSignedStringTransformer(
                new XmlObjectToBase64EncodedStringTransformer<>(),
                new SamlSignatureSigner<>(),
                responseAssertionEncrypter,
                responseAssertionSigner,
                new ResponseSignatureCreator(signatureFactory)
        );
    }
}
