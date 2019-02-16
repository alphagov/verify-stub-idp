package uk.gov.ida.stub.idp.auth;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * This is a means to install the filter, using the same IdpStubsRespository used elsewhere
 * If injected manually then it won't necessarily be the same as the one used elsewhere (hk2 created
 * objects are not managed by hk2 and therefore not re-used)
 */
public class ManagedAuthFilterInstaller implements Managed {

    private final StubIdpConfiguration stubIdpConfiguration;
    private final IdpStubsRepository idpStubsRepository;
    private final Environment environment;

    @Inject
    public ManagedAuthFilterInstaller(StubIdpConfiguration stubIdpConfiguration, IdpStubsRepository idpStubsRepository, Environment environment) {
        this.stubIdpConfiguration = stubIdpConfiguration;
        this.idpStubsRepository = idpStubsRepository;
        this.environment = environment;
    }

    @Override
    public void start() {
        if(stubIdpConfiguration.isBasicAuthEnabledForUserResource()) {
            environment.servlets()
                    .addFilter("Basic Auth Filter for Idps", new UserResourceBasicAuthFilter(idpStubsRepository))
                    .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }
    }

    @Override
    public void stop() {
        // method intentionally left blank
    }
}
