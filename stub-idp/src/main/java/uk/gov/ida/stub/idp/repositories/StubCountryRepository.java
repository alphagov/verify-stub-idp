package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdp;
import uk.gov.ida.stub.idp.configuration.UserCredentials;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.domain.factories.IdpStubsConfigurationFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class StubCountryRepository {

    public static final String STUB_COUNTRY_FRIENDLY_ID = EidasScheme.stub_country.getEidasSchemeName();

    private final IdpStubsConfigurationFactory idpStubsConfigurationFactory;
    private final AllIdpsUserRepository allIdpsUserRepository;
    private final String stubCountryMetadataUrl;

    @Inject
    public StubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl") String stubCountryMetadataUrl, IdpStubsConfigurationFactory idpStubsConfigurationFactory) {
        this.allIdpsUserRepository = allIdpsUserRepository;
        this.stubCountryMetadataUrl = stubCountryMetadataUrl;
        this.idpStubsConfigurationFactory = idpStubsConfigurationFactory;
        allIdpsUserRepository.createHardcodedTestUsersForCountries(STUB_COUNTRY_FRIENDLY_ID, STUB_COUNTRY_FRIENDLY_ID);
    }

    /**
     * This method is a little bit of a hack, to allow stub-country users to be used for multiple stub countries
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

    public List<UserCredentials> getUserCredentialsForFriendlyId(String friendlyId) {
        final Optional<IdpStubsConfiguration> idpStubsConfiguration = idpStubsConfigurationFactory.tryBuildDefault();
        if (!idpStubsConfiguration.isPresent()) {
            return Collections.emptyList();
        }

        final List<StubIdp> matchingCountries = idpStubsConfiguration.get().getStubCountries().stream()
                .filter(c -> c.getFriendlyId().equals(friendlyId))
                .collect(Collectors.toList());

        if (matchingCountries.isEmpty()) {
            return Collections.emptyList();
        }

        return matchingCountries.get(0).getIdpUserCredentials();
    }
}
