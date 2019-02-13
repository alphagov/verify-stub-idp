package uk.gov.ida.shared.utils.featuretoggles;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.shared.utils.featuretoggles.FeatureConfigurationBuilder.aFeatureConfiguration;
import static uk.gov.ida.shared.utils.featuretoggles.FeatureEntryBuilder.aFeatureEntry;
import static uk.gov.ida.shared.utils.featuretoggles.IdaFeatures.EncodeAssertions;
import static uk.gov.ida.shared.utils.featuretoggles.IdaFeatures.UIRework;

public class FeatureRepositoryTest {
    @Test
    public void should_loadActive() throws Exception {
        FeatureConfiguration configuration = aFeatureConfiguration()
                .withFeatureClass(IdaFeatures.class.getCanonicalName())
                .withFeature(aFeatureEntry().withFeatureName(UIRework.name()).isActive(true).build())
                .withFeature(aFeatureEntry().withFeatureName(EncodeAssertions.name()).isActive(false).build())
                .build();

        FeatureRepository systemUnderTest = new FeatureRepository();
        systemUnderTest.loadFeatures(configuration);

        assertThat(UIRework.isActive()).isTrue();
    }

    @Test
    public void should_loadInactive() throws Exception {
        FeatureConfiguration configuration = aFeatureConfiguration()
                .withFeatureClass(IdaFeatures.class.getCanonicalName())
                .withFeature(aFeatureEntry().withFeatureName(UIRework.name()).isActive(true).build())
                .withFeature(aFeatureEntry().withFeatureName(EncodeAssertions.name()).isActive(false).build())
                .build();

        FeatureRepository systemUnderTest = new FeatureRepository();
        systemUnderTest.loadFeatures(configuration);

        assertThat(EncodeAssertions.isActive()).isFalse();
    }
}    
