package uk.gov.ida.saml.metadata;

import com.codahale.metrics.health.HealthCheck.Result;
import com.google.common.collect.ImmutableMap;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.saml.core.test.builders.metadata.EntityDescriptorBuilder;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasTrustAnchorHealthCheckTest {

    @Mock
    private MetadataResolverRepository metadataResolverRepository;

    private EidasTrustAnchorHealthCheck eidasTrustAnchorHealthCheck;


    @Before
    public void setUp(){
        eidasTrustAnchorHealthCheck = new EidasTrustAnchorHealthCheck(metadataResolverRepository);
    }

    @Test
    public void shouldReturnHealthyWhenNoTrustAnchorsAreFound() {
        Result result = eidasTrustAnchorHealthCheck.check();

        assertThat(result.isHealthy()).isTrue();
    }

    @Test
    public void shouldReturnUnhealthyWhenAnyMetadataResolversDontContainMetadataMatchingTheEntityId() throws Exception {
        String entityId1 = "entityId1";
        String entityId2 = "entityId2";
        String entityId3 = "entityId3";
        List<String> entityIds = asList(entityId1, entityId2, entityId3);
        when(metadataResolverRepository.getTrustAnchorsEntityIds()).thenReturn(entityIds);

        MetadataResolver validMetadataResolver = getValidMetadataResolver(entityId1);
        MetadataResolver secondMetadataResolver = mock(MetadataResolver.class);
        MetadataResolver thirdMetadataResolver = mock(MetadataResolver.class);

        ImmutableMap<String, MetadataResolver> metadataResolverMap = ImmutableMap.of(
                entityId1, validMetadataResolver,
                entityId2, secondMetadataResolver,
                entityId3, thirdMetadataResolver
        );

        when(metadataResolverRepository.getResolverEntityIds()).thenReturn(entityIds);
        when(metadataResolverRepository.getMetadataResolvers()).thenReturn(metadataResolverMap);

        Result result = eidasTrustAnchorHealthCheck.check();

        assertThat(result.isHealthy()).isFalse();
        assertThat(((Map<String, String>)result.getDetails().get("unresolvedMetadata")).keySet()).contains(entityId2, entityId3);
        assertThat(((Map<String, String>)result.getDetails().get("unresolvedMetadata")).keySet()).doesNotContain(entityId1);
    }

    @Test
    public void shouldReturnUnhealthyMetadataResolversAreMissing() throws Exception {
        String entityId1 = "entityId1";
        String entityId2 = "entityId2";
        String entityId3 = "entityId3";
        List<String> entityIds = asList(entityId1, entityId2, entityId3);
        when(metadataResolverRepository.getTrustAnchorsEntityIds()).thenReturn(entityIds);

        MetadataResolver validMetadataResolver = getValidMetadataResolver(entityId1);

        ImmutableMap<String, MetadataResolver> metadataResolverMap = ImmutableMap.of(entityId1, validMetadataResolver);

        when(metadataResolverRepository.getResolverEntityIds()).thenReturn(asList(entityId1));
        when(metadataResolverRepository.getMetadataResolvers()).thenReturn(metadataResolverMap);

        Result result = eidasTrustAnchorHealthCheck.check();

        assertThat(result.isHealthy()).isFalse();
        assertThat((List<String>)result.getDetails().get("missingMetadataResolverEntityIds")).contains(entityId2, entityId3);
        assertThat((List<String>)result.getDetails().get("missingMetadataResolverEntityIds")).doesNotContain(entityId1);
    }

    @Test
    public void shouldReturnHealthyWhenAllMetadataResolversAreHealthy() throws Exception {
        String entityId1 = "entityId1";
        String entityId2 = "entityId2";
        List<String> entityIds = asList(entityId1, entityId2);
        when(metadataResolverRepository.getTrustAnchorsEntityIds()).thenReturn(entityIds);

        MetadataResolver validMetadataResolver1 = getValidMetadataResolver(entityId1);
        MetadataResolver validMetadataResolver2 = getValidMetadataResolver(entityId2);
        ImmutableMap<String, MetadataResolver> metadataResolverMap = ImmutableMap.of(entityId1, validMetadataResolver1, entityId2, validMetadataResolver2);

        when(metadataResolverRepository.getResolverEntityIds()).thenReturn(entityIds);
        when(metadataResolverRepository.getMetadataResolvers()).thenReturn(metadataResolverMap);

        Result result = eidasTrustAnchorHealthCheck.check();

        assertThat(result.isHealthy()).isTrue();
    }

    private MetadataResolver getValidMetadataResolver(String entityId) throws MarshallingException, SignatureException, ResolverException {
        MetadataResolver metadataResolver = mock(MetadataResolver.class);
        when(metadataResolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(entityId)))).thenReturn(EntityDescriptorBuilder.anEntityDescriptor().build());

        return metadataResolver;
    }
}
