package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.eidas.PlaceOfBirth;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class PlaceOfBirthUnmarshallerTest {
    @Test
    public void shouldUnmarshallPlaceOfBirth() throws Exception {
        final PlaceOfBirth placeOfBirth = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:PlaceOfBirthType\">\n" +
                "Peterborough" +
                "</saml2:AttributeValue>"
        );

        assertThat(placeOfBirth.getPlaceOfBirth()).isEqualTo("Peterborough");
    }
}
