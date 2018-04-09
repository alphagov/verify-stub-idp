package uk.gov.ida.stub.idp.security;

import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;

import javax.inject.Inject;
import java.security.PublicKey;

public class HubEncryptionKeyStore implements EncryptionKeyStore {

    private final MetadataRepository metadataRepository;
    private final PublicKeyFactory publicKeyFactory;

    @Inject
    public HubEncryptionKeyStore(MetadataRepository metadataRepository, PublicKeyFactory publicKeyFactory) {
        this.metadataRepository = metadataRepository;
        this.publicKeyFactory = publicKeyFactory;
    }

    @Override
    public PublicKey getEncryptionKeyForEntity(String entityId) {
        String encodedEncryptionCertificate = metadataRepository.getEncryptionCertificate();
        return publicKeyFactory.createPublicKey(encodedEncryptionCertificate);
    }
}
