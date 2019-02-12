package uk.gov.ida.saml.metadata.factories;

import com.google.common.base.Throwables;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.metadata.criteria.entity.impl.EntityDescriptorCriterionPredicateRegistry;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterChain;
import uk.gov.ida.saml.metadata.EntitiesDescriptorNameCriterion;
import uk.gov.ida.saml.metadata.EntitiesDescriptorNamePredicate;
import uk.gov.ida.saml.metadata.JerseyClientMetadataResolver;

import javax.ws.rs.client.Client;
import java.net.URI;
import java.util.List;
import java.util.Timer;

public class MetadataResolverFactory {

    public MetadataResolver create(Client client, URI metadataUri, List<MetadataFilter> metadataFilterList, long minRefreshDelay, long maxRefreshDelay) {
        try {
            InitializationService.initialize();
            JerseyClientMetadataResolver metadataResolver = new JerseyClientMetadataResolver(
                    new Timer(),
                    client,
                    metadataUri);
            BasicParserPool parserPool = new BasicParserPool();
            parserPool.initialize();
            metadataResolver.setParserPool(parserPool);
            metadataResolver.setId("MetadataModule.MetadataResolver");

            MetadataFilterChain metadataFilterChain = new MetadataFilterChain();
            metadataFilterChain.setFilters(metadataFilterList);
            metadataResolver.setMetadataFilter(metadataFilterChain);

            metadataResolver.setRequireValidMetadata(true);
            metadataResolver.setFailFastInitialization(false);
            metadataResolver.setMaxRefreshDelay(maxRefreshDelay);
            metadataResolver.setMinRefreshDelay(minRefreshDelay);
            metadataResolver.setResolveViaPredicatesOnly(true);

            EntityDescriptorCriterionPredicateRegistry registry = new EntityDescriptorCriterionPredicateRegistry();
            registry.register(EntitiesDescriptorNameCriterion.class, EntitiesDescriptorNamePredicate.class);
            metadataResolver.setCriterionPredicateRegistry(registry);

            metadataResolver.initialize();
            return metadataResolver;
        } catch (ComponentInitializationException | InitializationException e) {
            throw Throwables.propagate(e);
        }
    }
}
