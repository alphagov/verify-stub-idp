package uk.gov.ida.stub.idp.saml.transformers;

import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import uk.gov.ida.saml.core.api.CoreTransformersFactory;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;

import java.util.function.Function;

public class EidasResponseTransformerProvider {

    private final CoreTransformersFactory coreTransformersFactory;
    private final EncryptionKeyStore encryptionKeyStore;
    private final IdaKeyStore keyStore;
    private final EntityToEncryptForLocator entityToEncryptForLocator;
    private final SignatureAlgorithm signatureAlgorithm;
    private final DigestAlgorithm digestAlgorithm;

    public EidasResponseTransformerProvider(CoreTransformersFactory coreTransformersFactory,
                                            EncryptionKeyStore encryptionKeyStore,
                                            IdaKeyStore keyStore,
                                            EntityToEncryptForLocator entityToEncryptForLocator,
                                            SignatureAlgorithm signatureAlgorithm,
                                            DigestAlgorithm digestAlgorithm) {

        this.coreTransformersFactory = coreTransformersFactory;
        this.encryptionKeyStore = encryptionKeyStore;
        this.keyStore = keyStore;
        this.entityToEncryptForLocator = entityToEncryptForLocator;
        this.signatureAlgorithm = signatureAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
    }

    public Function<Response, String> getTransformer(){
        return coreTransformersFactory.getResponseStringTransformer(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                signatureAlgorithm,
                digestAlgorithm,
                new EncrypterFactory().withDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
    }
}
