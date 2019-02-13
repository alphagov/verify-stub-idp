package uk.gov.ida.shared.utils.optionals;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class OptionalHelperTest {

    private static final String VALUE = "foo";
    private static final Optional javaOptionalWithValue = Optional.of(VALUE);
    private static final Optional javaOptionalWithoutValue = Optional.empty();
    private static final com.google.common.base.Optional guavaOptionalWithValue = com.google.common.base.Optional.of(VALUE);
    private static final com.google.common.base.Optional guavaOptionalWithoutValue = com.google.common.base.Optional.absent();

    @Test
    public void toJavaOptionalValue() throws Exception {
        assertThat(OptionalHelper.toJavaOptional(guavaOptionalWithValue)).isEqualTo(javaOptionalWithValue);
        assertThat(OptionalHelper.toJavaOptional(guavaOptionalWithoutValue)).isEqualTo(javaOptionalWithoutValue);
    }

    @Test
    public void toGuavaptional() throws Exception {
        assertThat(OptionalHelper.toGuavaOptional(javaOptionalWithValue)).isEqualTo(guavaOptionalWithValue);
        assertThat(OptionalHelper.toGuavaOptional(javaOptionalWithoutValue)).isEqualTo(guavaOptionalWithoutValue);
    }

}