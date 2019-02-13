package uk.gov.ida.shared.utils.featuretoggles;

public class FeatureRepository {
    public void loadFeatures(FeatureConfiguration featureConfiguration) throws ClassNotFoundException, NoSuchFieldException {
        for (FeatureEntry featureEntry : featureConfiguration.getFeatures()) {
            Class featureClass = Class.forName(featureConfiguration.getFeatureClass());
            ((Feature)Enum.valueOf(featureClass, featureEntry.getFeatureName())).setActive(featureEntry.isActive());
        }
    }
}
