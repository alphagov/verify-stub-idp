package uk.gov.ida.shared.utils.email;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailValidatorTest {

    @Test
    public void assertInternationalEmailIsValid() throws Exception {
        assertThat(EmailValidator.isValid("björn.nußbaum@trouble.org")).isTrue();
    }

    @Test
    public void assertStandardEmailIsValid() throws Exception {
        assertThat(EmailValidator.isValid("bjorn.nussbaum@trouble.org")).isTrue();
    }

    @Test
    public void assertInvalidEmail() throws Exception {
        assertThat(EmailValidator.isValid("invalid")).isFalse();
    }
}
