package uk.gov.ida.saml.metadata.factories;

import com.google.common.base.Predicate;
import net.shibboleth.utilities.java.support.resolver.CriterionPredicateRegistry;
import org.junit.Test;
import org.mockito.Mock;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import uk.gov.ida.saml.metadata.EntitiesDescriptorNameCriterion;
import uk.gov.ida.saml.metadata.EntitiesDescriptorNamePredicate;
import uk.gov.ida.saml.metadata.ExpiredCertificateMetadataFilter;

import javax.ws.rs.client.Client;
import java.net.URI;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MetadataResolverFactoryTest {

    @Mock
    ExpiredCertificateMetadataFilter expiredCertificateMetadataFilter;

    @Mock
    SignatureValidationFilter signatureValidationFilter;

    @Test
    public void shouldProvideMetadataResolver() throws Exception {
        Client client = new org.glassfish.jersey.client.JerseyClientBuilder().build();
        MetadataResolverFactory metadataResolverFactory = new MetadataResolverFactory();
        MetadataResolver metadataResolver = metadataResolverFactory.create(client, URI.create("http://example.com"), asList(signatureValidationFilter,  expiredCertificateMetadataFilter),10, 5);
        assertThat(metadataResolver).isNotNull();

        AbstractBatchMetadataResolver batchMetadataResolver = (AbstractBatchMetadataResolver) metadataResolver;
        CriterionPredicateRegistry<EntityDescriptor> criterionPredicateRegistry = batchMetadataResolver.getCriterionPredicateRegistry();
        Predicate<EntityDescriptor> predicate = criterionPredicateRegistry.getPredicate(new EntitiesDescriptorNameCriterion("some-name"));
        assertThat(predicate.getClass()).isEqualTo(EntitiesDescriptorNamePredicate.class);
        assertThat(batchMetadataResolver.isResolveViaPredicatesOnly()).isTrue();
    }
}
