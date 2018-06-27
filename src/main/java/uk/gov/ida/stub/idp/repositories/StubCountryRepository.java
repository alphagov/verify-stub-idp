package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.stub.idp.domain.EidasScheme;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

import static java.text.MessageFormat.format;

public class StubCountryRepository {

    private final AllIdpsUserRepository allIdpsUserRepository;
    private final String stubCountryMetadataUrl;
    public static final String STUB_COUNTRY_FRIENDLY_ID = "stub-country";

    @Inject
    public StubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl) {
        this.allIdpsUserRepository = allIdpsUserRepository;
        this.stubCountryMetadataUrl = stubCountryMetadataUrl;
        allIdpsUserRepository.createHardcodedTestUsersForCountries(STUB_COUNTRY_FRIENDLY_ID, STUB_COUNTRY_FRIENDLY_ID);
    }

    /**
     * This method is a little bit of a hack, to allow stub-country users to be used for multiple stub countries
     * @param friendlyId ignored
     */
    public StubCountry getStubCountryWithFriendlyId(EidasScheme eidasScheme){
        return new StubCountry(
                eidasScheme.getEidasSchemeName(),
                format("Stub Country ({0})", eidasScheme.getEidasSchemeName()),
                STUB_COUNTRY_FRIENDLY_ID,
                UriBuilder.fromUri(stubCountryMetadataUrl).build(eidasScheme.getEidasSchemeName()).toString(),
                allIdpsUserRepository
        );
    }
}
