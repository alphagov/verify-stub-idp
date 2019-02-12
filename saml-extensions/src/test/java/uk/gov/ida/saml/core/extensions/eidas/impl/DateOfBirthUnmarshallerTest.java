package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class DateOfBirthUnmarshallerTest {
    @Test
    public void shouldUnmarshallDateOfBirth() throws Exception {
        final DateOfBirth dateOfBirth = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:DateOfBirthType\">\n" +
                "1965-01-01" +
                "</saml2:AttributeValue>"
        );

        assertThat(dateOfBirth.getDateOfBirth().toString()).isEqualTo("1965-01-01");
    }
}
