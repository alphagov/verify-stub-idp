package uk.gov.ida.shared.utils.featuretoggles;

public class FeatureEntryBuilder {
    public String featureName;
    public boolean isActive;

    public static FeatureEntryBuilder aFeatureEntry() {
        return new FeatureEntryBuilder();
    }

    public FeatureEntryBuilder withFeatureName(String featureName) {
        this.featureName = featureName;
        return this;
    }

    public FeatureEntryBuilder isActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public FeatureEntry build() {
        return new FeatureEntry(){
            @Override
            public String getFeatureName() {
                return FeatureEntryBuilder.this.featureName;
            }

            @Override
            public boolean isActive() {
                return FeatureEntryBuilder.this.isActive;
            }
        };
    }

}
