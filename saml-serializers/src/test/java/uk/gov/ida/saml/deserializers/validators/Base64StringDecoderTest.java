package uk.gov.ida.saml.deserializers.validators;

import org.apache.xml.security.utils.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static uk.gov.ida.saml.errors.SamlTransformationErrorFactory.invalidBase64Encoding;

@RunWith(MockitoJUnitRunner.class)
public class Base64StringDecoderTest {

    private Base64StringDecoder samlStringProcessor;

    @Before
    public void setup() {
        samlStringProcessor = new Base64StringDecoder();
    }

    @Test
    public void shouldPassDecodedStringToNextProcessor() {
        assertThat(samlStringProcessor.decode(toBase64Encoded("string"))).isEqualTo("string");
    }

    private String toBase64Encoded(String string) {
        return Base64.encode(string.getBytes());
    }

    @Test
    public void shouldPassDecodedMultiLineStringToNextProcessor() {
        assertThat(samlStringProcessor.decode(toBase64Encoded("string") + "\n" + toBase64Encoded("string")))
                .isEqualTo("string" + "string");
    }

    @Test
    public void shouldHandleNotBase64Encoded() {
        final String input = "<SAMLRequest>&lt;&gt;</SAMLRequest>";

        try {
            samlStringProcessor.decode(input);
            fail("Expected action to throw");
        } catch (SamlTransformationErrorException e) {
            SamlValidationSpecificationFailure failure = invalidBase64Encoding(input);
            assertThat(e.getMessage()).isEqualTo(failure.getErrorMessage());
            assertThat(e.getLogLevel()).isEqualTo(failure.getLogLevel());
        }
    }
}
