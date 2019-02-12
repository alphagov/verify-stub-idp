package uk.gov.ida.saml.metadata;

import com.codahale.metrics.health.HealthCheck;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EidasTrustAnchorHealthCheck extends HealthCheck {

    private final MetadataResolverRepository metadataResolverRepository;

    @Inject
    public EidasTrustAnchorHealthCheck(MetadataResolverRepository metadataResolverRepository) {
        this.metadataResolverRepository = metadataResolverRepository;
    }

    @Override
    protected Result check() {
        List<String> trustAnchorEntityIds = metadataResolverRepository.getTrustAnchorsEntityIds();

        List<String> missingEntityIds = getErrorsCreatingMetadataResolvers(trustAnchorEntityIds);
        Map<String, String> unresolvedMetadata = getErrorsResolvingMetadata();

        if (missingEntityIds.isEmpty() && unresolvedMetadata.isEmpty()) {
            return Result.healthy();
        }
        return Result.builder().unhealthy()
                .withDetail("missingMetadataResolverEntityIds", missingEntityIds)
                .withDetail("unresolvedMetadata", unresolvedMetadata)
                .build();
    }

    private List<String> getErrorsCreatingMetadataResolvers(List<String> trustAnchorEntityIds) {
        List<String> entityIdsWithResolver = metadataResolverRepository.getResolverEntityIds();

        if (trustAnchorEntityIds.size() > entityIdsWithResolver.size()) {
            List<String> missingMetadataResolverEntityIds = new ArrayList<>(trustAnchorEntityIds);
            missingMetadataResolverEntityIds.removeAll(entityIdsWithResolver);

            return missingMetadataResolverEntityIds;
        }
        return Collections.emptyList();
    }

    private Map<String, String> getErrorsResolvingMetadata() {
        Map<String, MetadataResolver> metadataResolvers = metadataResolverRepository.getMetadataResolvers();
        Map<String, String> errors = new HashMap<>();
        metadataResolvers.forEach((entityId, metadataResolver) -> {
            try {
                CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion(entityId));
                EntityDescriptor entityDescriptor = metadataResolver.resolveSingle(criteria);
                if (entityDescriptor == null){
                    errors.put(entityId, "Could not resolve metadata");
                }
            } catch (ResolverException e) {
                errors.put(entityId, e.getMessage());
            }
        });
        return errors;
    }
}
