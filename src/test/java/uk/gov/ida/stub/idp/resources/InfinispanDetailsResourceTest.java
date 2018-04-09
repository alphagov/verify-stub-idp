package uk.gov.ida.stub.idp.resources;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.shared.dropwizard.infinispan.config.CacheType;
import uk.gov.ida.shared.dropwizard.infinispan.config.InfinispanConfiguration;
import uk.gov.ida.shared.dropwizard.infinispan.util.InfinispanCacheManager;
import uk.gov.ida.shared.dropwizard.infinispan.util.InfinispanDetailsResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class InfinispanDetailsResourceTest {

    private InfinispanDetailsResource infinispanDetailsResource;
    @Mock
    private InfinispanConfiguration infinispanConfiguration;
    @Mock
    private InfinispanCacheManager infinispanCacheManager;

    @Before
    public void setUp() throws Exception {
        infinispanDetailsResource = new InfinispanDetailsResource(infinispanCacheManager, infinispanConfiguration);
    }

    @Test
    public void shouldDisplayExpectedClusterSize() throws Exception {

        when(infinispanConfiguration.getType()).thenReturn(CacheType.standalone);
        assertThat(infinispanDetailsResource.getInfinispanDetails().getExpectedClusterSize()).isEqualTo(1);

        when(infinispanConfiguration.getType()).thenReturn(CacheType.clustered);
        when(infinispanConfiguration.getInitialHosts()).thenReturn("localhost[7800],localhost[7801]");
        assertThat(infinispanDetailsResource.getInfinispanDetails().getExpectedClusterSize()).isEqualTo(2);

        when(infinispanConfiguration.getType()).thenReturn(CacheType.clustered);
        when(infinispanConfiguration.getInitialHosts()).thenReturn("localhost[7800],localhost[7801],localhost[7802]");
        assertThat(infinispanDetailsResource.getInfinispanDetails().getExpectedClusterSize()).isEqualTo(3);

    }
    
}