package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.eidas.Gender;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class GenderUnmarshallerTest {
    @Test
    public void shouldUnmarshallGender() throws Exception {
        final Gender gender = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:GenderType\">\n" +
                "Male" +
                "</saml2:AttributeValue>"
        );

        assertThat(gender.getValue()).isEqualTo("Male");
    }
}
