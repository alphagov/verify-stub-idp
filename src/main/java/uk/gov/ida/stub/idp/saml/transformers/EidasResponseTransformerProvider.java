package uk.gov.ida.stub.idp.saml.transformers;

import com.google.common.base.Optional;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.stub.idp.domain.factories.StubTransformersFactory;

import java.util.function.Function;

public class EidasResponseTransformerProvider {

    private final StubTransformersFactory stubTransformersFactory;
    private final EncryptionKeyStore encryptionKeyStore;
    private final IdaKeyStore keyStore;
    private final EntityToEncryptForLocator entityToEncryptForLocator;
    private final SignatureAlgorithm signatureAlgorithm;
    private final DigestAlgorithm digestAlgorithm;

    public EidasResponseTransformerProvider(StubTransformersFactory stubTransformersFactory,
                                            EncryptionKeyStore encryptionKeyStore,
                                            IdaKeyStore keyStore,
                                            EntityToEncryptForLocator entityToEncryptForLocator,
                                            SignatureAlgorithm signatureAlgorithm,
                                            DigestAlgorithm digestAlgorithm) {

        this.stubTransformersFactory = stubTransformersFactory;
        this.encryptionKeyStore = encryptionKeyStore;
        this.keyStore = keyStore;
        this.entityToEncryptForLocator = entityToEncryptForLocator;
        this.signatureAlgorithm = signatureAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
    }

    public Function<Response, String> getTransformer(){
        return stubTransformersFactory.getEidasResponseToStringTransformer(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                signatureAlgorithm,
                digestAlgorithm);
    }
}
