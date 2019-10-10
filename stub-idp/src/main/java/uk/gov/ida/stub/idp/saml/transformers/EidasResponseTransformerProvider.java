package uk.gov.ida.stub.idp.saml.transformers;

import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.security.EncrypterFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.stub.idp.domain.factories.StubCoreTransformersFactory;

import java.util.function.Function;

import static org.opensaml.xmlsec.encryption.support.EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM;

public class EidasResponseTransformerProvider {

    private final EncryptionKeyStore encryptionKeyStore;
    private final IdaKeyStore keyStore;
    private final EntityToEncryptForLocator entityToEncryptForLocator;
    private final SignatureAlgorithm signatureAlgorithm;
    private final DigestAlgorithm digestAlgorithm;

    public EidasResponseTransformerProvider(EncryptionKeyStore encryptionKeyStore,
                                            IdaKeyStore keyStore,
                                            EntityToEncryptForLocator entityToEncryptForLocator,
                                            SignatureAlgorithm signatureAlgorithm,
                                            DigestAlgorithm digestAlgorithm) {

        this.encryptionKeyStore = encryptionKeyStore;
        this.keyStore = keyStore;
        this.entityToEncryptForLocator = entityToEncryptForLocator;
        this.signatureAlgorithm = signatureAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
    }

    public Function<Response, String> getTransformer(boolean signAssertions) {
        return StubCoreTransformersFactory.getResponseStringTransformer(
            encryptionKeyStore,
            keyStore,
            entityToEncryptForLocator,
            signatureAlgorithm,
            digestAlgorithm,
            new EncrypterFactory().withDataEncryptionAlgorithm(ALGO_ID_BLOCKCIPHER_AES256_GCM),
            signAssertions);
    }
}
