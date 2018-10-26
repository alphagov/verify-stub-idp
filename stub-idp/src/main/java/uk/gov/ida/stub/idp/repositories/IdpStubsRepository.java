package uk.gov.ida.stub.idp.repositories;

import io.dropwizard.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdp;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.configuration.UserCredentials;
import uk.gov.ida.stub.idp.domain.factories.IdpStubsConfigurationFactory;
import uk.gov.ida.stub.idp.exceptions.IdpNotFoundException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class IdpStubsRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IdpStubsRepository.class);

    private Map<String, Idp> repo = Collections.emptyMap();
    private Map<String, List<UserCredentials>> userCredentialsMap = Collections.emptyMap();
    private final AllIdpsUserRepository allIdpsUserRepository;
    private final StubIdpConfiguration stubIdpConfiguration;
    private final IdpStubsConfigurationFactory idpStubsConfigurationFactory;

    @Inject
    public IdpStubsRepository(AllIdpsUserRepository allIdpsUserRepository, StubIdpConfiguration stubIdpConfiguration, IdpStubsConfigurationFactory idpStubsConfigurationFactory) {
        this.allIdpsUserRepository = allIdpsUserRepository;
        this.stubIdpConfiguration = stubIdpConfiguration;
        this.idpStubsConfigurationFactory = idpStubsConfigurationFactory;
        load(stubIdpConfiguration.getStubIdpsYmlFileLocation());
    }

    public void load(String stubIdpsYmlFileLocation) {
        IdpStubsConfiguration idpStubConfiguration;

        try {
            idpStubConfiguration = idpStubsConfigurationFactory.build(stubIdpsYmlFileLocation);
        } catch (IOException | ConfigurationException e) {
            LOG.error("Error parsing configuration file, stubs remain unchanged", e);
            return;
        }

        Collection<StubIdp> stubIdps = idpStubConfiguration.getStubIdps();

        LOG.info("Loading into IdpStubsRepository.");

        Map<String, Idp> tempIdpMap = new HashMap<>();
        Map<String, List<UserCredentials>> tempUserCredMap = new HashMap<>();
        String entityIdTemplate = stubIdpConfiguration.getSamlConfiguration().getEntityId();
        for (StubIdp stubIdp : stubIdps) {
            Idp idp = new Idp(
                    stubIdp.getDisplayName(),
                    stubIdp.getFriendlyId(),
                    stubIdp.getAssetId(),
                    stubIdp.getSendKeyInfo(),
                    format(entityIdTemplate, stubIdp.getFriendlyId()),
                    allIdpsUserRepository
            );
            tempIdpMap.put(stubIdp.getFriendlyId(), idp);
            tempUserCredMap.put(stubIdp.getFriendlyId(), stubIdp.getIdpUserCredentials());
            if (!repo.containsKey(stubIdp.getFriendlyId())) {
                LOG.info("Creating IDP: " + idp.getFriendlyId());
                allIdpsUserRepository.createHardcodedTestUsersForIdp(idp.getFriendlyId(), idp.getAssetId());
                LOG.info("IDP Created: " + idp.getFriendlyId());
            }
        }

        // this is seemingly because we want the change to be atomic rather than wipe and re-buildDefault in-place
        repo = tempIdpMap;
        userCredentialsMap = tempUserCredMap;
    }


    public Idp getIdpWithFriendlyId(String friendlyId) {
        Idp idp = repo.get(friendlyId);
        if (idp == null) {
            throw new IdpNotFoundException("No idp found with friendlyId: " + friendlyId);
        }
        return idp;
    }

    public List<UserCredentials> getUserCredentialsForFriendlyId(String friendlyId) {
        List<UserCredentials> idpUserCredList = userCredentialsMap.get(friendlyId);
        if (idpUserCredList == null) {
            LOG.error("Trying to get user credentials for friendly name: " + friendlyId + " which returned an empty list. Is this idp configured correctly?");
            idpUserCredList = Collections.emptyList();
        }

        return idpUserCredList;
    }

}
