package uk.gov.ida.stub.idp.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stub.idp.exceptions.StubCountryNotFoundException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

public class StubCountryRepository {

    private static final Logger LOG = LoggerFactory.getLogger(StubCountryRepository.class);

    private final AllIdpsUserRepository allIdpsUserRepository;
    private final String stubCountryMetadataUrl;
    private StubCountry stubCountry;

    @Inject
    public StubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl) {
        this.allIdpsUserRepository = allIdpsUserRepository;
        this.stubCountryMetadataUrl = stubCountryMetadataUrl;
        load();
    }

    private void load() {

        String issuerId = UriBuilder.fromUri(stubCountryMetadataUrl).build("stub-country").toString();

        LOG.info("Loading into StubCountryRepository.");

        stubCountry = new StubCountry(
                "stub-country",
                "Stub Country",
                "stub-country",
                issuerId,
                allIdpsUserRepository
        );
            LOG.info("Stub Country user is being created");
            allIdpsUserRepository.createHardcodedTestUsersForIdp(stubCountry.getFriendlyId(), stubCountry.getAssetId());
            LOG.info("Stub Country user has been created");
    }

    public StubCountry getStubCountryWithFriendlyId(String friendlyId){
        if (!stubCountry.getFriendlyId().equals(friendlyId)){
            throw new StubCountryNotFoundException("Stub Country doesn't exist with friendlyId: " + friendlyId);
        }
        return stubCountry;
    }
}
