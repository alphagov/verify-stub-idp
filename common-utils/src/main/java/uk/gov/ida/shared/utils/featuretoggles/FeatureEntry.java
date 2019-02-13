package uk.gov.ida.shared.utils.featuretoggles;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class FeatureEntry {

    @Valid
    @NotNull
    public String featureName;

    @Valid
    @NotNull
    public boolean active;

    public String getFeatureName() { return featureName;
    }

    public boolean isActive() {
        return active;
    }
}
