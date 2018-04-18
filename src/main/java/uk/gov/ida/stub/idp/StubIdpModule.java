package uk.gov.ida.stub.idp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractReloadingMetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.w3c.dom.Document;
import uk.gov.ida.common.shared.configuration.KeyConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyConfiguration;
import uk.gov.ida.common.shared.configuration.SecureCookieKeyStore;
import uk.gov.ida.common.shared.security.HmacDigest;
import uk.gov.ida.common.shared.security.IdGenerator;
import uk.gov.ida.common.shared.security.PublicKeyFactory;
import uk.gov.ida.common.shared.security.SecureCookieKeyConfigurationKeyStore;
import uk.gov.ida.common.shared.security.X509CertificateFactory;
import uk.gov.ida.saml.core.api.CoreTransformersFactory;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.saml.idp.configuration.SamlConfiguration;
import uk.gov.ida.saml.metadata.MetadataHealthCheck;
import uk.gov.ida.saml.metadata.MetadataResolverConfiguration;
import uk.gov.ida.saml.metadata.factories.DropwizardMetadataResolverFactory;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.SignatureFactory;
import uk.gov.ida.saml.security.SigningKeyStore;
import uk.gov.ida.stub.idp.auth.ManagedAuthFilterInstaller;
import uk.gov.ida.stub.idp.builders.CountryMetadataBuilder;
import uk.gov.ida.stub.idp.builders.CountryMetadataSigningHelper;
import uk.gov.ida.stub.idp.configuration.AssertionLifetimeConfiguration;
import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.SigningKeyPairConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.cookies.HmacValidator;
import uk.gov.ida.stub.idp.domain.factories.AssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.AssertionRestrictionsFactory;
import uk.gov.ida.stub.idp.domain.factories.IdentityProviderAssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.StubTransformersFactory;
import uk.gov.ida.stub.idp.listeners.StubIdpsFileListener;
import uk.gov.ida.stub.idp.repositories.AllIdpsUserRepository;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.repositories.UserRepository;
import uk.gov.ida.stub.idp.repositories.jdbc.JDBIEidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.jdbc.JDBIIdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.jdbc.JDBIUserRepository;
import uk.gov.ida.stub.idp.repositories.jdbc.UserMapper;
import uk.gov.ida.stub.idp.saml.locators.IdpHardCodedEntityToEncryptForLocator;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;
import uk.gov.ida.stub.idp.security.HubEncryptionKeyStore;
import uk.gov.ida.stub.idp.security.IdaAuthnRequestKeyStore;
import uk.gov.ida.stub.idp.services.AuthnRequestReceiverService;
import uk.gov.ida.stub.idp.services.EidasAuthnResponseService;
import uk.gov.ida.stub.idp.services.GeneratePasswordService;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.StubCountryService;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.UserService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;
import uk.gov.ida.truststore.EmptyKeyStoreProvider;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Validator;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class StubIdpModule extends AbstractModule {

    private final Bootstrap<StubIdpConfiguration> bootstrap;

    public static final String HUB_CONNECTOR_METADATA_REPOSITORY = "HubConnectorMetadataRepository";
    public static final String HUB_METADATA_REPOSITORY = "HubMetadataRepository";
    public static final String HUB_CONNECTOR_METADATA_RESOLVER = "HubConnectorMetadataResolver";
    public static final String HUB_METADATA_RESOLVER = "HubMetadataResolver";
    public static final String HUB_CONNECTOR_ENCRYPTION_KEY_STORE = "HubConnectorEncryptionKeyStore";
    public static final String HUB_ENCRYPTION_KEY_STORE = "HubEncryptionKeyStore";
    public static final String COUNTRY_SIGNING_KEY_STORE = "CountrySigningKeyStore";
    public static final String IDP_SIGNING_KEY_STORE = "IdpSigningKeyStore";

    public StubIdpModule(Bootstrap<StubIdpConfiguration> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    protected void configure() {
        bind(AssertionLifetimeConfiguration.class).to(StubIdpConfiguration.class).asEagerSingleton();

        bind(SigningKeyStore.class).to(IdaAuthnRequestKeyStore.class).asEagerSingleton();

        bind(EntityToEncryptForLocator.class).to(IdpHardCodedEntityToEncryptForLocator.class).asEagerSingleton();
        bind(CountryMetadataSigningHelper.class).asEagerSingleton();
        bind(new TypeLiteral<ConcurrentMap<String, Document>>() {
        }).toInstance(new ConcurrentHashMap<>());

        bind(AllIdpsUserRepository.class).asEagerSingleton();

        bind(IdpStubsRepository.class).asEagerSingleton();
        bind(KeyStore.class).toProvider(EmptyKeyStoreProvider.class).asEagerSingleton();

        bind(PublicKeyFactory.class);
        bind(SamlResponseRedirectViewFactory.class);
        bind(AssertionFactory.class);
        bind(AssertionRestrictionsFactory.class);
        bind(IdentityProviderAssertionFactory.class);
        bind(CountryMetadataBuilder.class);

        bind(StubIdpsFileListener.class).asEagerSingleton();

        //must be eager singletons to be auto injected
        // Elegant-hack: this is how we install the basic auth filter, so we can use a guice injected user repository
        bind(ManagedAuthFilterInstaller.class).asEagerSingleton();

        bind(IdGenerator.class);
        bind(X509CertificateFactory.class);

        bind(AuthnRequestReceiverService.class);
        bind(SuccessAuthnResponseService.class);
        bind(EidasAuthnResponseService.class);
        bind(GeneratePasswordService.class);
        bind(NonSuccessAuthnResponseService.class);
        bind(IdpUserService.class);
        bind(StubCountryService.class);
        bind(UserService.class);
        bind(SamlResponseRedirectViewFactory.class);
        
        bind(new TypeLiteral<SessionRepository<IdpSession>>(){}).to(IdpSessionRepository.class);
        bind(new TypeLiteral<SessionRepository<EidasSession>>(){}).to(EidasSessionRepository.class);

        bind(HmacValidator.class);
        bind(HmacDigest.class);
        bind(SecureCookieKeyStore.class).to(SecureCookieKeyConfigurationKeyStore.class);
        bind(CookieFactory.class);
    }

    @Provides
    public ObjectMapper getObjectMapper() {
        return bootstrap.getObjectMapper();
    }

    @Provides
    public UserMapper getUserMapper(ObjectMapper objectMapper) {
        return new UserMapper(objectMapper);
    }

    @Provides
    public UserRepository getUserRepository(StubIdpConfiguration configuration, UserMapper userMapper) {
        Jdbi jdbi = Jdbi.create(configuration.getDatabaseConfiguration().getUrl());
        return new JDBIUserRepository(jdbi, userMapper);
    }
    
    @Provides
    @Singleton
    public IdpSessionRepository getIdpSessionRepository(StubIdpConfiguration configuration) {
        Jdbi jdbi = Jdbi.create(configuration.getDatabaseConfiguration().getUrl());
        return new JDBIIdpSessionRepository(jdbi);
    }

    @Provides
    @Singleton
    public EidasSessionRepository getEidasSessionRepository(StubIdpConfiguration configuration) {
        Jdbi jdbi = Jdbi.create(configuration.getDatabaseConfiguration().getUrl());
        return new JDBIEidasSessionRepository(jdbi);
    }

    @Provides
    @Singleton
    @Named("HubEntityId")
    public String getHubEntityId(StubIdpConfiguration configuration) {
        return configuration.getHubEntityId();
    }

    @Provides
    @Singleton
    @Named("HubConnectorEntityId")
    public String getHubConnectorEntityId(StubIdpConfiguration configuration) {
        return configuration.getEuropeanIdentityConfiguration().getHubConnectorEntityId();
    }

    @Provides
    @Singleton
    @Named("StubCountryMetadataUrl")
    public String getStubCountryMetadataUrl(StubIdpConfiguration configuration) {
        return configuration.getEuropeanIdentityConfiguration().getStubCountryBaseUrl() + Urls.METADATA_RESOURCE;
    }

    @Provides
    @Singleton
    @Named("StubCountrySsoUrl")
    public String getStubCountrySsoUrl(StubIdpConfiguration configuration) {
        return configuration.getEuropeanIdentityConfiguration().getStubCountryBaseUrl() + Urls.EIDAS_SAML2_SSO_RESOURCE;
    }

    @Provides
    private ConfigurationFactory<IdpStubsConfiguration> getConfigurationFactory() {
        Validator validator = bootstrap.getValidatorFactory().getValidator();
        return new DefaultConfigurationFactoryFactory<IdpStubsConfiguration>()
            .create(IdpStubsConfiguration.class, validator, bootstrap.getObjectMapper(), "");
    }

    @Provides
    @Singleton
    @Named("sessionCacheTimeoutInMinutes")
    public Integer getSessionCacheTimeoutInMinutes() {
        return 180;
    }

    @Provides
    private ConfigurationSourceProvider getConfigurationSourceProvider() {
        return bootstrap.getConfigurationSourceProvider();
    }

    @Provides
    SignatureAlgorithm provideSignatureAlgorithm() {
        return new SignatureRSASHA256();
    }

    @Provides
    DigestAlgorithm provideDigestAlgorithm() {
        return new DigestSHA256();
    }

    @Provides
    @Named("countryMetadataSignatureFactory")
    private SignatureFactory getSignatureFactoryWithKeyInfo(@Named(COUNTRY_SIGNING_KEY_STORE) IdaKeyStore keyStore, DigestAlgorithm digestAlgorithm, SignatureAlgorithm signatureAlgorithm) {
        return new SignatureFactory(true, new IdaKeyStoreCredentialRetriever(keyStore), signatureAlgorithm, digestAlgorithm);
    }

    @Provides
    public StubCountryRepository getStubCountryRepository(AllIdpsUserRepository allIdpsUserRepository, @Named("StubCountryMetadataUrl")String stubCountryMetadataUrl){
        return new StubCountryRepository(allIdpsUserRepository, stubCountryMetadataUrl);
    }

    @Provides
    @Named("metadataValidityPeriod")
    private ReadablePeriod getMetadataValidity() {
        return new Period().withYears(100);
    }

    @Provides
    public Function<String, IdaAuthnRequestFromHub> getStringToIdaAuthnRequestFromHubTransformer(SigningKeyStore signingKeyStore) {
        return new StubTransformersFactory().getStringToIdaAuthnRequestFromHub(
            signingKeyStore
        );
    }

    @Provides
    public Function<String, AuthnRequest> getStringToAuthnRequestTransformer() {
        return new StubTransformersFactory().getStringToAuthnRequest();
    }

    @Provides
    public OutboundResponseFromIdpTransformerProvider getOutboundResponseFromIdpTransformerProvider(
        @Named(StubIdpModule.HUB_ENCRYPTION_KEY_STORE) EncryptionKeyStore encryptionKeyStore,
        @Named(IDP_SIGNING_KEY_STORE) IdaKeyStore keyStore,
        EntityToEncryptForLocator entityToEncryptForLocator,
        StubIdpConfiguration stubIdpConfiguration) {
        return new OutboundResponseFromIdpTransformerProvider(
            encryptionKeyStore,
            keyStore,
            entityToEncryptForLocator,
            Optional.ofNullable(stubIdpConfiguration.getSigningKeyPairConfiguration().getCert()),
            new StubTransformersFactory(),
            new SignatureRSASHA256(),
            new DigestSHA256()
        );
    }

    @Provides
    public EidasResponseTransformerProvider getEidasResponseTransformerProvider(
        @Named(StubIdpModule.HUB_CONNECTOR_ENCRYPTION_KEY_STORE) Optional<EncryptionKeyStore> encryptionKeyStore,
        @Named(COUNTRY_SIGNING_KEY_STORE) IdaKeyStore keyStore,
        EntityToEncryptForLocator entityToEncryptForLocator) {
        return new EidasResponseTransformerProvider(
            new CoreTransformersFactory(),
            encryptionKeyStore.orElse(null),
            keyStore,
            entityToEncryptForLocator,
            new SignatureRSASHA256(),
            new DigestSHA256()
        );
    }

    @Provides
    @Singleton
    public SamlConfiguration samlConfiguration(StubIdpConfiguration stubIdpConfiguration) {
        return stubIdpConfiguration.getSamlConfiguration();
    }

    @Provides
    @Singleton
    @Named(IDP_SIGNING_KEY_STORE)
    public IdaKeyStore getKeyStore(StubIdpConfiguration stubIdpConfiguration) {
        return getKeystoreFromConfig(stubIdpConfiguration.getSigningKeyPairConfiguration());
    }

    @Provides
    @Singleton
    @Named(COUNTRY_SIGNING_KEY_STORE)
    public IdaKeyStore getCountryKeyStore(StubIdpConfiguration stubIdpConfiguration) {
        return getKeystoreFromConfig(stubIdpConfiguration.getEuropeanIdentityConfiguration().getSigningKeyPairConfiguration());
    }

    @Provides
    @Singleton
    @Named("isSecureCookieEnabled")
    public Boolean isSecureCookieEnabled(StubIdpConfiguration stubIdpConfiguration) {
        return stubIdpConfiguration.getSecureCookieConfiguration() != null;
    }

    @Provides
    @Singleton
    public HmacDigest.HmacSha256MacFactory getHmacSha256MacFactory() {
        return new HmacDigest.HmacSha256MacFactory();
    }

    @Provides
    @Singleton
    @SecureCookieKeyConfiguration
    public KeyConfiguration getSecureCookieKeyConfiguration(StubIdpConfiguration stubIdpConfiguration) {
        return isSecureCookieEnabled(stubIdpConfiguration) ? stubIdpConfiguration.getSecureCookieConfiguration().getKeyConfiguration() : new KeyConfiguration() {
        };
    }

    @Provides
    @Singleton
    public SecureCookieConfiguration getSecureCookieConfiguration(StubIdpConfiguration stubIdpConfiguration) {
        return isSecureCookieEnabled(stubIdpConfiguration) ? stubIdpConfiguration.getSecureCookieConfiguration() : new SecureCookieConfiguration() {
            {
                this.secure = false;
            }
        };
    }

    @Provides
    @Named(HUB_ENCRYPTION_KEY_STORE)
    @Singleton
    public EncryptionKeyStore getHubEncryptionKeyStore(@Named(HUB_METADATA_REPOSITORY) MetadataRepository metadataRepository, PublicKeyFactory publicKeyFactory) {
        return new HubEncryptionKeyStore(metadataRepository, publicKeyFactory);
    }

    @Provides
    @Named(HUB_CONNECTOR_ENCRYPTION_KEY_STORE)
    @Singleton
    public Optional<EncryptionKeyStore> getHubConnectorEncryptionKeyStore(@Named(HUB_CONNECTOR_METADATA_REPOSITORY) Optional<MetadataRepository> metadataRepository, PublicKeyFactory publicKeyFactory) {
        if (metadataRepository.isPresent()) {
            return Optional.of(new HubEncryptionKeyStore(metadataRepository.get(), publicKeyFactory));
        }
        return Optional.empty();
    }

    @Provides
    @Named(HUB_METADATA_REPOSITORY)
    @Singleton
    public MetadataRepository getHubMetadataRepository(@Named(HUB_METADATA_RESOLVER) MetadataResolver metadataResolver, @Named("HubEntityId") String hubEntityId) {
        return new MetadataRepository(metadataResolver, hubEntityId);
    }

    @Provides
    @Named(HUB_CONNECTOR_METADATA_REPOSITORY)
    @Singleton
    public Optional<MetadataRepository> getHubConnectorMetadataRepository(@Named(HUB_CONNECTOR_METADATA_RESOLVER) Optional<MetadataResolver> metadataResolver, @Named("HubConnectorEntityId") String hubEntityId) {
        if (metadataResolver.isPresent()) {
            return Optional.of(new MetadataRepository(metadataResolver.get(), hubEntityId));
        }
        return Optional.empty();
    }

    @Provides
    @Named(HUB_METADATA_RESOLVER)
    @Singleton
    public MetadataResolver getHubMetadataResolver(Environment environment, StubIdpConfiguration configuration) {
        MetadataResolver metadataResolver = new DropwizardMetadataResolverFactory().createMetadataResolver(environment, configuration.getMetadataConfiguration());
        registerMetadataHealthcheckAndRefresh(environment, metadataResolver, configuration.getMetadataConfiguration(), "metadata");
        return metadataResolver;
    }

    @Provides
    @Named(HUB_CONNECTOR_METADATA_RESOLVER)
    @Singleton
    public Optional<MetadataResolver> getHubConnectorMetadataResolver(Environment environment, StubIdpConfiguration configuration) {
        if (configuration.getEuropeanIdentityConfiguration().isEnabled()) {
            MetadataResolver metadataResolver = new DropwizardMetadataResolverFactory().createMetadataResolver(environment, configuration.getEuropeanIdentityConfiguration().getMetadata());
            registerMetadataHealthcheckAndRefresh(environment, metadataResolver, configuration.getEuropeanIdentityConfiguration().getMetadata(), "connector-metadata");
            return Optional.of(metadataResolver);
        }
        return Optional.empty();
    }

    private void registerMetadataHealthcheckAndRefresh(Environment environment, MetadataResolver metadataResolver, MetadataResolverConfiguration metadataResolverConfiguration, String name) {
        String expectedEntityId = metadataResolverConfiguration.getExpectedEntityId();
        MetadataHealthCheck metadataHealthCheck = new MetadataHealthCheck(metadataResolver, expectedEntityId);
        environment.healthChecks().register(name, metadataHealthCheck);

        environment.admin().addTask(new Task(name + "-refresh") {
            @Override
            public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
                ((AbstractReloadingMetadataResolver) metadataResolver).refresh();
            }
        });
    }

    private IdaKeyStore getKeystoreFromConfig(SigningKeyPairConfiguration keyPairConfiguration) {
        PrivateKey privateSigningKey = keyPairConfiguration.getPrivateKey();
        X509Certificate signingCertificate = new X509CertificateFactory().createCertificate(keyPairConfiguration.getCert());
        PublicKey publicSigningKey = signingCertificate.getPublicKey();
        KeyPair signingKeyPair = new KeyPair(publicSigningKey, privateSigningKey);

        return new IdaKeyStore(signingCertificate, signingKeyPair, Collections.emptyList());
    }
}
