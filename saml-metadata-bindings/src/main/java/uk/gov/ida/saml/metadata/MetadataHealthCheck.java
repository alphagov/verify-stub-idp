package uk.gov.ida.saml.metadata;

import com.codahale.metrics.health.HealthCheck;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import javax.inject.Inject;
import javax.inject.Named;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;

public class MetadataHealthCheck extends HealthCheck {
    private final MetadataResolver metadataResolver;
    private final String expectedEntityId;
    private final String name;

    @Inject
    public MetadataHealthCheck(MetadataResolver metadataProvider,
           @Named("expectedEntityId") String expectedEntityId) {
        this(metadataProvider, "metadata", expectedEntityId);
    }

    @Inject
    public MetadataHealthCheck(MetadataResolver metadataProvider,
            String name,
            @Named("expectedEntityId") String expectedEntityId) {
        this.metadataResolver = metadataProvider;
        this.name = name;
        this.expectedEntityId = expectedEntityId;
    }

    public String getName() {
        return name;
    }

    @Override
    protected Result check() throws Exception {
        CriteriaSet criteria = new CriteriaSet(new EntityIdCriterion(expectedEntityId));
        EntityDescriptor entityDescriptor = metadataResolver.resolveSingle(criteria);
        if (entityDescriptor != null) {
            return healthy();
        }
        return unhealthy("Could not load: " + expectedEntityId + " from the metadata provider");
    }
}
