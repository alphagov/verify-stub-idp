package uk.gov.ida.stub.idp.saml.transformers;


import com.google.common.base.Optional;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.domain.factories.StubTransformersFactory;
import uk.gov.ida.stub.idp.repositories.Idp;

import java.util.function.Function;

public class OutboundResponseFromIdpTransformerProvider {
    private final EncryptionKeyStore encryptionKeyStore;
    private final IdaKeyStore keyStore;
    private final EntityToEncryptForLocator entityToEncryptForLocator;
    private final Optional<String> publicSigningKey;
    private final StubTransformersFactory stubTransformersFactory;
    private final SignatureAlgorithm signatureAlgorithm;
    private final DigestAlgorithm digestAlgorithm;

    public OutboundResponseFromIdpTransformerProvider(
            EncryptionKeyStore encryptionKeyStore,
            IdaKeyStore keyStore,
            EntityToEncryptForLocator entityToEncryptForLocator,
            Optional<String> publicSigningKey,
            StubTransformersFactory stubTransformersFactory,
            SignatureAlgorithm signatureAlgorithm,
            DigestAlgorithm digestAlgorithm) {
        this.encryptionKeyStore = encryptionKeyStore;
        this.keyStore = keyStore;
        this.entityToEncryptForLocator = entityToEncryptForLocator;
        this.publicSigningKey = publicSigningKey;
        this.stubTransformersFactory = stubTransformersFactory;
        this.signatureAlgorithm = signatureAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
    }

    private Function<OutboundResponseFromIdp, String> getTransformerWithKeyInfo(String issuerId) {
        return stubTransformersFactory.getOutboundResponseFromIdpToStringTransformer(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                publicSigningKey.get(),
                issuerId,
                signatureAlgorithm,
                digestAlgorithm
        );
    }

    private Function<OutboundResponseFromIdp, String> getTransformer() {
        return stubTransformersFactory.getOutboundResponseFromIdpToStringTransformer(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                signatureAlgorithm,
                digestAlgorithm
        );
    }

    public Function<OutboundResponseFromIdp, String> get(Idp idp) {
        if (publicSigningKey.isPresent() && idp.shouldSendKeyInfo()) {
            return getTransformerWithKeyInfo(idp.getIssuerId());
        } else {
            return getTransformer();
        }
    }
}
