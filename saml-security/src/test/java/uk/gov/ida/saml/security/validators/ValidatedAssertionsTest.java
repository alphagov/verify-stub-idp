package uk.gov.ida.saml.security.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import uk.gov.ida.saml.security.saml.OpenSAMLRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.security.saml.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.security.saml.builders.AttributeStatementBuilder.anAttributeStatement;

@RunWith(OpenSAMLRunner.class)
public class ValidatedAssertionsTest {
    @Test
    public void should_returnMatchingDatasetAssertion() {
        Assertion mdsAssertion = anAssertion().addAttributeStatement(anAttributeStatement().build()).build();
        List<Assertion> assertions = asList(anAssertion().build(), mdsAssertion);

        ValidatedAssertions validatedAssertions = new ValidatedAssertions(assertions);

        assertThat(validatedAssertions.getMatchingDatasetAssertion().get()).isEqualTo(mdsAssertion);
    }

    @Test
    public void should_returnAuthnStatementAssertion() {
        Assertion mdsAssertion = anAssertion().addAttributeStatement(anAttributeStatement().build()).build();
        Assertion authnStatementAssertion = anAssertion().build();
        List<Assertion> assertions = asList(mdsAssertion, authnStatementAssertion);

        ValidatedAssertions validatedAssertions = new ValidatedAssertions(assertions);

        assertThat(validatedAssertions.getAuthnStatementAssertion().get()).isEqualTo(authnStatementAssertion);
    }

    @Test
    public void should_supportAnEmptyListOfAssertions() {
        ValidatedAssertions validatedAssertions = new ValidatedAssertions(emptyList());

        assertThat(validatedAssertions.getAuthnStatementAssertion().isPresent()).isFalse();
        assertThat(validatedAssertions.getMatchingDatasetAssertion().isPresent()).isFalse();
    }
}
