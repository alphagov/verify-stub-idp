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
    public static final String STUB_COUNTRY_FRIENDLY_ID = "stub-country";
    private StubCountry stubCountry;

    @Inject
    public StubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl) {
        this.allIdpsUserRepository = allIdpsUserRepository;
        this.stubCountryMetadataUrl = stubCountryMetadataUrl;
        load();
    }

    private void load() {

        String issuerId = UriBuilder.fromUri(stubCountryMetadataUrl).build(STUB_COUNTRY_FRIENDLY_ID).toString();

        LOG.info("Loading into StubCountryRepository.");

        stubCountry = new StubCountry(
                STUB_COUNTRY_FRIENDLY_ID,
                "Stub Country",
                STUB_COUNTRY_FRIENDLY_ID,
                issuerId,
                allIdpsUserRepository
        );
            LOG.info("Stub Country user is being created");
            allIdpsUserRepository.createHardcodedTestUsersForCountries(stubCountry.getFriendlyId(), stubCountry.getAssetId());
            LOG.info("Stub Country user has been created");
    }

    public StubCountry getStubCountryWithFriendlyId(String friendlyId){
        if (!stubCountry.getFriendlyId().equals(friendlyId)){
            throw new StubCountryNotFoundException("Stub Country doesn't exist with friendlyId: " + friendlyId);
        }
        return stubCountry;
    }
}
