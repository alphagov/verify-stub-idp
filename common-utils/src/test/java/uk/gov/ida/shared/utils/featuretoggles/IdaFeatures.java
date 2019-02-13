package uk.gov.ida.shared.utils.featuretoggles;

public enum IdaFeatures implements Feature {

    UIRework,
    EncodeAssertions;

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
