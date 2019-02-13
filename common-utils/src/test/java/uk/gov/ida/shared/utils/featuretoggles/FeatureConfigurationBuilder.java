package uk.gov.ida.shared.utils.featuretoggles;

import java.util.ArrayList;
import java.util.List;

public class FeatureConfigurationBuilder {
    private String featureClassA;
    private final List<FeatureEntry> featureEntriesA = new ArrayList<>();

    public static FeatureConfigurationBuilder aFeatureConfiguration() {
        return new FeatureConfigurationBuilder();
    }

    public FeatureConfigurationBuilder withFeatureClass(String featureClass) {
        this.featureClassA = featureClass;
        return this;
    }

    public FeatureConfigurationBuilder withFeature(FeatureEntry feature) {
        featureEntriesA.add(feature);
        return this;
    }

    public FeatureConfiguration build() {
        return new FeatureConfiguration(){
            @Override
            public List<FeatureEntry> getFeatures() {
                return featureEntriesA;
            }
            @Override
            public String getFeatureClass() {
                return featureClassA;
            }
        };
    }
}
