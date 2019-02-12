package uk.gov.ida.saml.hub.validators;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.saml.core.test.SamlTransformationErrorManagerTestHelper;
import uk.gov.ida.saml.hub.errors.SamlTransformationErrorFactory;

@RunWith(MockitoJUnitRunner.class)
public class StringSizeValidatorTest {

    @Test
    public void shouldPassIfStringSizeIsBetweenLowerAndUpperLimits() throws Exception {
        StringSizeValidator validator = new StringSizeValidator();

        String input = "This is between 10 and 30";

        validator.validate(input, 10, 30);
    }

    @Test
    public void shouldFailIfStringSizeIsLessThanLowerLimit() throws Exception {
        final StringSizeValidator validator = new StringSizeValidator();

        final String input = "Ring";

        SamlTransformationErrorManagerTestHelper.validateFail(
                new SamlTransformationErrorManagerTestHelper.Action() {
                    @Override
                    public void execute() {
                        validator.validate(input, 10, 30);
                    }
                },
                SamlTransformationErrorFactory.stringTooSmall(4, 10)
        );
    }

    @Test
    public void shouldFailIfStringSizeIsMoreThanUpperLimit() throws Exception {
        final StringSizeValidator validator = new StringSizeValidator();

        final String input = "Ring a ring";

        SamlTransformationErrorManagerTestHelper.validateFail(
                new SamlTransformationErrorManagerTestHelper.Action() {
                    @Override
                    public void execute() {
                        validator.validate(input, 0, 5);
                    }
                },
                SamlTransformationErrorFactory.stringTooLarge(11, 5)
        );
    }
}
