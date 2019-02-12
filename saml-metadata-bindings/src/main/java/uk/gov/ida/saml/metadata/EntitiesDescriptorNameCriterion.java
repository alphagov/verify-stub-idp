package uk.gov.ida.saml.metadata;

import net.shibboleth.utilities.java.support.resolver.Criterion;

public class EntitiesDescriptorNameCriterion implements Criterion {
    private final String expectedName;

    public EntitiesDescriptorNameCriterion(String expectedName) {
        this.expectedName = expectedName;
    }

    public String getExpectedName() {
        return expectedName;
    }
}
