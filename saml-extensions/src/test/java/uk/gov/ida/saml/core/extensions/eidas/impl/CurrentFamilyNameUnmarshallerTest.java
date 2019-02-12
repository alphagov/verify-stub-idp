package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class CurrentFamilyNameUnmarshallerTest {
    @Test
    public void shouldUnmarshallCurrentFamilyNameValue() throws Exception {
        final CurrentFamilyName currentFamilyName = Utils.unmarshall(getCurrentFamilyNameSamlString(true));

        assertThat(currentFamilyName.getFamilyName()).isEqualTo("Garcia");
    }

    @Test
    public void shouldUnmarshallLatinScriptValueWhenAbsent() throws Exception {
        final CurrentFamilyName currentFamilyName = Utils.unmarshall(getCurrentFamilyNameSamlString(true));

        assertThat(currentFamilyName.isLatinScript()).isEqualTo(true);
    }

    @Test
    public void shouldUnmarshallLatinScriptValueWhenPresent() throws Exception {
        final CurrentFamilyName currentFamilyName = Utils.unmarshall(getCurrentFamilyNameSamlString(false));

        assertThat(currentFamilyName.isLatinScript()).isEqualTo(false);
    }

    private String getCurrentFamilyNameSamlString(boolean isLatinScript) {
        return String.format(
                "<saml2:AttributeValue " +
                "%s" +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:CurrentFamilyNameType\">\n" +
                "Garcia" +
                "</saml2:AttributeValue>", isLatinScript ? "" : "LatinScript=\"false\"");
    }
}
