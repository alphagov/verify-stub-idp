package uk.gov.ida.saml.security.validators;


import org.opensaml.saml.saml2.core.Assertion;

import java.util.List;
import java.util.Optional;

public class ValidatedAssertions {
    private List<Assertion> assertions;

    public ValidatedAssertions(List<Assertion> assertions) {
        this.assertions = assertions;
    }

    public List<Assertion> getAssertions() {
        return assertions;
    }

    public Optional<Assertion> getMatchingDatasetAssertion() {
        return assertions.stream()
                .filter(this::isMatchingDatasetAssertion)
                .findFirst();
    }

    public Optional<Assertion> getAuthnStatementAssertion() {
        return assertions.stream()
                .filter(assertion -> !this.isMatchingDatasetAssertion(assertion))
                .findFirst();
    }

    private boolean isMatchingDatasetAssertion(Assertion assertion) {
        return !assertion.getAttributeStatements().isEmpty() && assertion.getAuthnStatements().isEmpty();
    }
}
