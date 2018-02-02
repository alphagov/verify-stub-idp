package uk.gov.ida.stub.idp;

import com.google.common.base.Optional;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.setup.Bootstrap;
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
import uk.gov.ida.notification.saml.translation.EidasResponseBuilder;
import uk.gov.ida.saml.core.api.CoreTransformersFactory;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.saml.idp.configuration.SamlConfiguration;
import uk.gov.ida.saml.security.EncryptionKeyStore;
import uk.gov.ida.saml.security.EntityToEncryptForLocator;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.security.IdaKeyStoreCredentialRetriever;
import uk.gov.ida.saml.security.SignatureFactory;
import uk.gov.ida.saml.security.SigningKeyStore;
import uk.gov.ida.shared.dropwizard.infinispan.util.InfinispanCacheManager;
import uk.gov.ida.stub.idp.auth.ManagedAuthFilterInstaller;
import uk.gov.ida.stub.idp.configuration.AssertionLifetimeConfiguration;
import uk.gov.ida.stub.idp.configuration.IdpStubsConfiguration;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.cookies.HmacValidator;
import uk.gov.ida.stub.idp.domain.factories.AssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.AssertionRestrictionsFactory;
import uk.gov.ida.stub.idp.domain.factories.IdentityProviderAssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.StubTransformersFactory;
import uk.gov.ida.stub.idp.listeners.StubIdpsFileListener;
import uk.gov.ida.stub.idp.repositories.AllIdpsUserRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.UserRepository;
import uk.gov.ida.stub.idp.repositories.infinispan.InfinispanUserRepository;
import uk.gov.ida.stub.idp.saml.locators.IdpHardCodedEntityToEncryptForLocator;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;
import uk.gov.ida.stub.idp.security.HubEncryptionKeyStore;
import uk.gov.ida.stub.idp.security.IdaAuthnRequestKeyStore;
import uk.gov.ida.stub.idp.services.AuthnRequestReceiverService;
import uk.gov.ida.stub.idp.services.EidasSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.GeneratePasswordService;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.UserService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;
import uk.gov.ida.truststore.EmptyKeyStoreProvider;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.Validator;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class StubIdpModule extends AbstractModule {

    private final Provider<InfinispanCacheManager> infinispanCacheManagerProvider;
    private final Bootstrap<StubIdpConfiguration> bootstrap;

    public StubIdpModule(final Provider<InfinispanCacheManager> infinispanCacheManagerProvider, Bootstrap<StubIdpConfiguration> bootstrap) {
        this.infinispanCacheManagerProvider = infinispanCacheManagerProvider;
        this.bootstrap = bootstrap;
    }

    @Override
    protected void configure() {
        bind(InfinispanCacheManager.class).toProvider(infinispanCacheManagerProvider);
        bind(AssertionLifetimeConfiguration.class).to(StubIdpConfiguration.class).asEagerSingleton();

        bind(EncryptionKeyStore.class).to(HubEncryptionKeyStore.class).asEagerSingleton();
        bind(SigningKeyStore.class).to(IdaAuthnRequestKeyStore.class).asEagerSingleton();

        bind(EntityToEncryptForLocator.class).to(IdpHardCodedEntityToEncryptForLocator.class).asEagerSingleton();
        bind(IdaKeyStoreCredentialRetriever.class).asEagerSingleton();
        bind(SignatureFactory.class).asEagerSingleton();
        bind(SessionRepository.class).asEagerSingleton();
        bind(new TypeLiteral<ConcurrentMap<String, Document>>() {}).toInstance(new ConcurrentHashMap<>());
        bind(MetadataRepository.class).asEagerSingleton();

        bind(AllIdpsUserRepository.class).asEagerSingleton();

        bind(IdpStubsRepository.class).asEagerSingleton();
        bind(KeyStore.class).toProvider(EmptyKeyStoreProvider.class).asEagerSingleton();

        bind(PublicKeyFactory.class);
        bind(SamlResponseRedirectViewFactory.class);
        bind(AssertionFactory.class);
        bind(AssertionRestrictionsFactory.class);
        bind(IdentityProviderAssertionFactory.class);

        bind(StubIdpsFileListener.class).asEagerSingleton();

        //must be eager singletons to be auto injected
        // Elegant-hack: this is how we install the basic auth filter, so we can use a guice injected user repository
        bind(ManagedAuthFilterInstaller.class).asEagerSingleton();

        bind(IdGenerator.class);
        bind(X509CertificateFactory.class);

        bind(AuthnRequestReceiverService.class);
        bind(SuccessAuthnResponseService.class);
        bind(EidasSuccessAuthnResponseService.class);
        bind(GeneratePasswordService.class);
        bind(NonSuccessAuthnResponseService.class);
        bind(IdpUserService.class);
        bind(UserService.class);
        bind(SamlResponseRedirectViewFactory.class);

        bind(UserRepository.class).to(InfinispanUserRepository.class);

        bind(HmacValidator.class);
        bind(HmacDigest.class);
        bind(SecureCookieKeyStore.class).to(SecureCookieKeyConfigurationKeyStore.class);
        bind(CookieFactory.class);
    }

    @Provides
    @Singleton
    @Named("HubEntityId")
    public String getHubEntityId(StubIdpConfiguration configuration) {
        return configuration.getHubEntityId();
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
    public Function<String, IdaAuthnRequestFromHub> getStringToIdaAuthnRequestFromHubTransformer(SigningKeyStore signingKeyStore) {
        return new StubTransformersFactory().getStringToIdaAuthnRequestFromHub(
                signingKeyStore
        );
    }

    @Provides
    public  Function<String, AuthnRequest> getStringToAuthnRequestTransformer(){
        return new StubTransformersFactory().getStringToAuthnRequest();
    }

    @Provides
    public OutboundResponseFromIdpTransformerProvider getOutboundResponseFromIdpTransformerProvider(EncryptionKeyStore encryptionKeyStore, IdaKeyStore keyStore, EntityToEncryptForLocator entityToEncryptForLocator, StubIdpConfiguration stubIdpConfiguration) {
        return new OutboundResponseFromIdpTransformerProvider(
                encryptionKeyStore,
                keyStore,
                entityToEncryptForLocator,
                Optional.fromNullable(stubIdpConfiguration.getSigningKeyPairConfiguration().getCert()),
                new StubTransformersFactory(),
                new SignatureRSASHA256(),
                new DigestSHA256()
        );
    }

    @Provides
    public EidasResponseTransformerProvider getEidasResponseTransformerProvider(EncryptionKeyStore encryptionKeyStore, IdaKeyStore keyStore, EntityToEncryptForLocator entityToEncryptForLocator) {
        return new EidasResponseTransformerProvider(
                new CoreTransformersFactory(),
                encryptionKeyStore,
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
    public IdaKeyStore getKeyStore(StubIdpConfiguration stubIdpConfiguration) {
        PrivateKey privateSigningKey = stubIdpConfiguration.getSigningKeyPairConfiguration().getPrivateKey();
        PublicKey publicSigningKey = new X509CertificateFactory().createCertificate(stubIdpConfiguration.getSigningKeyPairConfiguration().getCert()).getPublicKey();
        KeyPair signingKeyPair = new KeyPair(publicSigningKey, privateSigningKey);

        return new IdaKeyStore(signingKeyPair, Collections.emptyList());
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
        return isSecureCookieEnabled(stubIdpConfiguration)?stubIdpConfiguration.getSecureCookieConfiguration().getKeyConfiguration():new KeyConfiguration() {};
    }

    @Provides
    @Singleton
    public SecureCookieConfiguration getSecureCookieConfiguration(StubIdpConfiguration stubIdpConfiguration) {
        return isSecureCookieEnabled(stubIdpConfiguration)?stubIdpConfiguration.getSecureCookieConfiguration():new SecureCookieConfiguration() { { this.secure = false; } };
    }

    @Provides
    @Singleton
    public EidasResponseBuilder getEidasResponseBuilder(StubIdpConfiguration configuration){
        return new EidasResponseBuilder(configuration.getConnectorNodeUrl().toString(),
                configuration.getStubCountryMetadataUrl().toString(),
                configuration.getConnectorNodeIssuerId());
    }
}
