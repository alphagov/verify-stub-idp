package uk.gov.ida.shared.utils.featuretoggles;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FeatureConfigurationTest {

    @Test
    public void loadFromFile_shouldCreateConfiguration() throws Exception {
        Yaml yaml = new Yaml(new Constructor(FeatureConfiguration.class));
        String fileName = "uk/gov/ida/shared/utils/featuretoggles/feature-toggles.yml";
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);

        // Parse the YAML file and return the output as a series of Maps and Lists
        FeatureConfiguration featureConfiguration = (FeatureConfiguration) yaml.load(is);

        assertThat(featureConfiguration.getFeatureClass()).isEqualTo("uk.gov.ida.shared.utils.featuretoggles.IdaFeatures");
        assertThat(featureConfiguration.getFeatures().size()).isEqualTo(2);
        assertThat(featureConfiguration.getFeatures().get(0).isActive()).isEqualTo(false);
        assertThat(featureConfiguration.getFeatures().get(0).getFeatureName()).isEqualTo("UIRework");
        assertThat(featureConfiguration.getFeatures().get(1).isActive()).isEqualTo(true);
        assertThat(featureConfiguration.getFeatures().get(1).getFeatureName()).isEqualTo("EncodeAssertions");
    }

    @Test
    public void loadFromFile_shouldCreateConfigurationForEmptyFeatureList() throws Exception {
        Yaml yaml = new Yaml(new Constructor(FeatureConfiguration.class));
        String fileName = "uk/gov/ida/shared/utils/featuretoggles/no-features.yml";
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);

        // Parse the YAML file and return the output as a series of Maps and Lists
        FeatureConfiguration featureConfiguration = (FeatureConfiguration) yaml.load(is);

        assertThat(featureConfiguration.getFeatureClass()).isEqualTo("uk.gov.ida.shared.utils.featuretoggles.IdaFeatures");
        assertThat(featureConfiguration.getFeatures().size()).isEqualTo(0);
        assertThat(featureConfiguration.getFeatures()).isNotNull();
    }
}
