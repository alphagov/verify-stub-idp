package uk.gov.ida.stub.idp;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.hubspot.dropwizard.guicier.DropwizardModule;
import com.hubspot.dropwizard.guicier.GuiceBundle;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.freemarker.FreemarkerViewRenderer;
import uk.gov.ida.bundles.LoggingBundle;
import uk.gov.ida.bundles.MonitoringBundle;
import uk.gov.ida.bundles.ServiceStatusBundle;
import uk.gov.ida.filters.AcceptLanguageFilter;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.stub.idp.bundles.DatabaseMigrationBundle;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.exceptions.mappers.CatchAllExceptionMapper;
import uk.gov.ida.stub.idp.exceptions.mappers.FeatureNotEnabledExceptionMapper;
import uk.gov.ida.stub.idp.exceptions.mappers.FileNotFoundExceptionMapper;
import uk.gov.ida.stub.idp.exceptions.mappers.IdpNotFoundExceptionMapper;
import uk.gov.ida.stub.idp.exceptions.mappers.IdpUserNotFoundExceptionMapper;
import uk.gov.ida.stub.idp.exceptions.mappers.SessionSerializationExceptionMapper;
import uk.gov.ida.stub.idp.filters.NoCacheResponseFilter;
import uk.gov.ida.stub.idp.filters.SecurityHeadersFilter;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASessionFeature;
import uk.gov.ida.stub.idp.filters.StubIdpCacheControlFilter;
import uk.gov.ida.stub.idp.healthcheck.DatabaseHealthCheck;
import uk.gov.ida.stub.idp.healthcheck.StubIdpHealthCheck;
import uk.gov.ida.stub.idp.resources.AuthnRequestReceiverResource;
import uk.gov.ida.stub.idp.resources.GeneratePasswordResource;
import uk.gov.ida.stub.idp.resources.UserResource;
import uk.gov.ida.stub.idp.resources.eidas.EidasConsentResource;
import uk.gov.ida.stub.idp.resources.eidas.EidasDebugPageResource;
import uk.gov.ida.stub.idp.resources.eidas.EidasLoginPageResource;
import uk.gov.ida.stub.idp.resources.eidas.EidasProxyNodeServiceMetadataResource;
import uk.gov.ida.stub.idp.resources.eidas.EidasRegistrationPageResource;
import uk.gov.ida.stub.idp.resources.idp.ConsentResource;
import uk.gov.ida.stub.idp.resources.idp.DebugPageResource;
import uk.gov.ida.stub.idp.resources.idp.HeadlessIdpResource;
import uk.gov.ida.stub.idp.resources.idp.LoginPageResource;
import uk.gov.ida.stub.idp.resources.idp.RegistrationPageResource;
import uk.gov.ida.stub.idp.resources.idp.SingleIdpPromptPageResource;
import uk.gov.ida.stub.idp.resources.idp.CancelPreRegistrationPageResource;
import uk.gov.ida.stub.idp.resources.idp.LogoutPageResource;
import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Map;

public class StubIdpApplication extends Application<StubIdpConfiguration> {

    public static void main(String[] args) {
        JerseyGuiceUtils.reset();

        try {
            if (args == null || args.length == 0) {
                String configFile = System.getenv("CONFIG_FILE");

                if (configFile == null) {
                    throw new RuntimeException("CONFIG_FILE environment variable should be set with path to configuration file");
                }

                new StubIdpApplication().run("server", configFile);
            } else {
                new StubIdpApplication().run(args);
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public String getName() {
        return "Stub Idp Service";
    }

    @Override
    public final void initialize(Bootstrap<StubIdpConfiguration> bootstrap) {

        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)
            )
        );

        bootstrap.addBundle(new DatabaseMigrationBundle());

        GuiceBundle<StubIdpConfiguration> guiceBundle = GuiceBundle
            .defaultBuilder(getConfigurationClass())
            .modules(new StubIdpModule(bootstrap),
                new DropwizardModule())
            .build();
        bootstrap.addBundle(guiceBundle);

        bootstrap.addBundle(new ServiceStatusBundle());
        bootstrap.addBundle(new ViewBundle<StubIdpConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(StubIdpConfiguration config) {
                // beware: this is to force enable escaping of unsanitised user input
                return ImmutableMap.of(new FreemarkerViewRenderer().getSuffix(),
                    ImmutableMap.of(
                        "output_format", "HTMLOutputFormat"
                    ));
            }
        });
        bootstrap.addBundle(new LoggingBundle());
        bootstrap.addBundle(new MonitoringBundle());

        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
        bootstrap.getObjectMapper().registerModule(new Jdk8Module());
    }

    @Override
    public final void run(StubIdpConfiguration configuration, Environment environment) {
        IdaSamlBootstrap.bootstrap();
        environment.servlets().addFilter("Cache Control", new StubIdpCacheControlFilter(configuration)).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/consent");
        environment.servlets().addFilter("Remove Accept-Language headers", AcceptLanguageFilter.class).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        environment.jersey().register(SessionCookieValueMustExistAsASessionFeature.class);

        environment.getObjectMapper().setDateFormat(new ISO8601DateFormat());

        //resources
        environment.jersey().register(AuthnRequestReceiverResource.class);
        environment.jersey().register(LoginPageResource.class);
        environment.jersey().register(EidasLoginPageResource.class);
        environment.jersey().register(EidasConsentResource.class);
        environment.jersey().register(RegistrationPageResource.class);
        environment.jersey().register(CancelPreRegistrationPageResource.class);
        environment.jersey().register(EidasRegistrationPageResource.class);
        environment.jersey().register(DebugPageResource.class);
        environment.jersey().register(ConsentResource.class);
        environment.jersey().register(UserResource.class);
        environment.jersey().register(HeadlessIdpResource.class);
        environment.jersey().register(GeneratePasswordResource.class);
        environment.jersey().register(EidasProxyNodeServiceMetadataResource.class);
        environment.jersey().register(EidasDebugPageResource.class);
        environment.jersey().register(SingleIdpPromptPageResource.class);
        environment.jersey().register(LogoutPageResource.class);

        //exception mappers
        environment.jersey().register(IdpNotFoundExceptionMapper.class);
        environment.jersey().register(IdpUserNotFoundExceptionMapper.class);
        environment.jersey().register(FileNotFoundExceptionMapper.class);
        environment.jersey().register(SessionSerializationExceptionMapper.class);
        environment.jersey().register(FeatureNotEnabledExceptionMapper.class);
        environment.jersey().register(CatchAllExceptionMapper.class);

        //filters
        environment.jersey().register(NoCacheResponseFilter.class);
        environment.jersey().register(SecurityHeadersFilter.class);

        //health checks
        StubIdpHealthCheck healthCheck = new StubIdpHealthCheck();
        environment.healthChecks().register(healthCheck.getName(), healthCheck);

        DatabaseHealthCheck dbHealthCheck = new DatabaseHealthCheck(configuration.getDatabaseConfiguration().getUrl());
        environment.healthChecks().register("database", dbHealthCheck);
    }
}
