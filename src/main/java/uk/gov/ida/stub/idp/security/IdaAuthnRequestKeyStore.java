package uk.gov.ida.stub.idp.security;

import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.saml.security.SigningKeyStore;
import uk.gov.ida.stub.idp.StubIdpModule;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

public class IdaAuthnRequestKeyStore implements SigningKeyStore {
    private final MetadataRepository metadataRepository;
    private final PublicKeyFactory publicKeyFactory;

    @Inject
    public IdaAuthnRequestKeyStore(@Named(StubIdpModule.HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository, PublicKeyFactory publicKeyFactory) {
        this.metadataRepository = metadataRepository;
        this.publicKeyFactory = publicKeyFactory;
    }

    @Override
    public List<PublicKey> getVerifyingKeysForEntity(String entityId) {
        return metadataRepository.getSigningCertificates().stream()
                .map(publicKeyFactory::createPublicKey)
                .collect(Collectors.toList());
    }
}
