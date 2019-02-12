package uk.gov.ida.saml.metadata;

import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.X509CertUtils;
import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import org.apache.commons.collections.ListUtils;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.eidas.trustanchor.CountryTrustAnchor;
import uk.gov.ida.saml.metadata.factories.DropwizardMetadataResolverFactory;
import uk.gov.ida.saml.metadata.factories.MetadataSignatureTrustEngineFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class EidasMetadataResolverRepository implements MetadataResolverRepository {

    private final Logger log = LoggerFactory.getLogger(EidasMetadataResolverRepository.class);
    private final EidasTrustAnchorResolver trustAnchorResolver;
    private final DropwizardMetadataResolverFactory dropwizardMetadataResolverFactory;
    private final MetadataResolverConfigBuilder metadataResolverConfigBuilder;
    private ImmutableMap<String, MetadataResolverContainer> metadataResolvers = ImmutableMap.of();
    private List<JWK> trustAnchors = new ArrayList<>();
    private final EidasMetadataConfiguration eidasMetadataConfiguration;
    private final Timer timer;
    private final MetadataSignatureTrustEngineFactory metadataSignatureTrustEngineFactory;
    private long delayBeforeNextRefresh;
    private Client client;

    @Inject
    public EidasMetadataResolverRepository(EidasTrustAnchorResolver trustAnchorResolver,
                                           EidasMetadataConfiguration eidasMetadataConfiguration,
                                           DropwizardMetadataResolverFactory dropwizardMetadataResolverFactory,
                                           Timer timer,
                                           MetadataSignatureTrustEngineFactory metadataSignatureTrustEngineFactory,
                                           MetadataResolverConfigBuilder metadataResolverConfigBuilder,
                                           Client client
    ) {
        this.timer = timer;
        this.trustAnchorResolver = trustAnchorResolver;
        this.eidasMetadataConfiguration = eidasMetadataConfiguration;
        this.dropwizardMetadataResolverFactory = dropwizardMetadataResolverFactory;
        this.metadataSignatureTrustEngineFactory = metadataSignatureTrustEngineFactory;
        this.metadataResolverConfigBuilder = metadataResolverConfigBuilder;
        this.client = client;
        refresh();
    }

    @Override
    public Optional<MetadataResolver> getMetadataResolver(String entityId) {
        return Optional.ofNullable(metadataResolvers.get(entityId)).map(MetadataResolverContainer::getMetadataResolver);
    }

    @Override
    public List<String> getResolverEntityIds() {
        return metadataResolvers.keySet().asList();
    }

    @Override
    public Optional<ExplicitKeySignatureTrustEngine> getSignatureTrustEngine(String entityId) {
        return Optional.ofNullable(metadataResolvers.get(entityId)).map(MetadataResolverContainer::getSignatureTrustEngine);
    }

    @Override
    public Map<String, MetadataResolver> getMetadataResolvers() {
        return metadataResolvers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getMetadataResolver()
                ));
    }

    @Override
    public List<String> getTrustAnchorsEntityIds() {
        return trustAnchors.stream().map(JWK::getKeyID).collect(toList());
    }

    private JWK getTrustAnchorFromKeyId(String keyId) {
        return trustAnchors.stream().filter(e -> e.getKeyID().equals(keyId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find " + keyId + " in trust anchors"));
    }

    @Override
    public void refresh() {
        delayBeforeNextRefresh = eidasMetadataConfiguration.getTrustAnchorMaxRefreshDelay();

        try {
            trustAnchors = trustAnchorResolver.getTrustAnchors();
            refreshMetadataResolvers();
        } catch (Exception e) {
            log.error("Error fetching trust anchor or validating it", e);
            setShortRefreshDelay();
        } finally {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refresh();
                }
            }, delayBeforeNextRefresh);
        }
    }

    private void refreshMetadataResolvers() {
        List<String> trustAnchorsEntityIds = getTrustAnchorsEntityIds();
        List<String> currentResolverEntityIds = getResolverEntityIds();

        List<String> resolversToRemove = ListUtils.subtract(currentResolverEntityIds, trustAnchorsEntityIds);
        ImmutableMap.Builder<String, MetadataResolverContainer> newMetadataResolvers = new ImmutableMap.Builder<>();

        trustAnchorsEntityIds.forEach(trustAnchorsEntityId -> {
            try {
                MetadataResolverContainer metadataResolverContainer = metadataResolvers.containsKey(trustAnchorsEntityId) ?
                        metadataResolvers.get(trustAnchorsEntityId) : createMetadataResolverContainer(trustAnchorsEntityId);
                newMetadataResolvers.put(trustAnchorsEntityId, metadataResolverContainer);
            } catch (Exception e) {
                log.error("Error creating MetadataResolver for " + trustAnchorsEntityId, e);
            }
        });

        List<JerseyClientMetadataResolver> metadataResolversToRemove = resolversToRemove.stream()
                .map(resolverToRemove -> metadataResolvers.get(resolverToRemove).getMetadataResolver())
                .collect(toList());

        this.metadataResolvers = newMetadataResolvers.build();

        metadataResolversToRemove.forEach(AbstractInitializableComponent::destroy);
    }

    private MetadataResolverContainer createMetadataResolverContainer(String resolverToAddEntityId) throws CertificateException, UnsupportedEncodingException, ComponentInitializationException {
        JWK trustAnchor = getTrustAnchorFromKeyId(resolverToAddEntityId);

        Collection<String> errors = CountryTrustAnchor.findErrors(trustAnchor);

        if (!errors.isEmpty()) {
            throwExceptionIfCertificateExpiredMessagePresent(errors);
            throw new Error(String.format("Managed to generate an invalid anchor: %s", String.join(", ", errors)));
        }

        Date metadataSigningCertExpiryDate = sortCertsByDate(trustAnchor).get(0).getNotAfter();
        Date nextRunTime = DateTime.now().plus(delayBeforeNextRefresh).toDate();
        if (metadataSigningCertExpiryDate.before(nextRunTime)) {
            setShortRefreshDelay();
        }

        return createMetadataResolverContainer(trustAnchor);
    }

    @Override
    public List<X509Certificate> sortCertsByDate(JWK trustAnchor) {
        return trustAnchor.getX509CertChain().stream()
                .map(base64 -> {
                    try {
                        return X509CertUtils.parse(Base64.decode(String.valueOf(base64)));
                    } catch (Base64DecodingException e) {
                        throw new IllegalArgumentException(String.format("Failed to parse X509 certificate: %s", e.getMessage()));
                    }
                })
                .sorted(Comparator.comparing(X509Certificate::getNotAfter))
                .collect(toList());
    }

    private MetadataResolverContainer createMetadataResolverContainer(JWK trustAnchor) throws CertificateException, ComponentInitializationException, UnsupportedEncodingException {
        MetadataResolverConfiguration metadataResolverConfiguration = metadataResolverConfigBuilder.createMetadataResolverConfiguration(trustAnchor, eidasMetadataConfiguration);
        JerseyClientMetadataResolver metadataResolver = (JerseyClientMetadataResolver) dropwizardMetadataResolverFactory.createMetadataResolverWithClient(metadataResolverConfiguration, true, client);
        return new MetadataResolverContainer(metadataResolver, metadataSignatureTrustEngineFactory.createSignatureTrustEngine(metadataResolver));
    }

    private void setShortRefreshDelay() {
        delayBeforeNextRefresh = eidasMetadataConfiguration.getTrustAnchorMinRefreshDelay();
    }

    private class MetadataResolverContainer {
        private final JerseyClientMetadataResolver metadataResolver;
        private final ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine;

        private MetadataResolverContainer(JerseyClientMetadataResolver metadataResolver,
                                          ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine) {
            this.metadataResolver = metadataResolver;
            this.explicitKeySignatureTrustEngine = explicitKeySignatureTrustEngine;
        }

        private ExplicitKeySignatureTrustEngine getSignatureTrustEngine() {
            return explicitKeySignatureTrustEngine;
        }

        private JerseyClientMetadataResolver getMetadataResolver() {
            return metadataResolver;
        }
    }

    private void throwExceptionIfCertificateExpiredMessagePresent(Collection<String> errors) throws CertificateException {
        Optional<String> certExpiryErrorMessage = errors.stream()
            .filter(message -> message.contains("X.509 certificate has expired"))
            .findFirst();
        if (certExpiryErrorMessage.isPresent()) throw new CertificateException(certExpiryErrorMessage.get());
    }
}
