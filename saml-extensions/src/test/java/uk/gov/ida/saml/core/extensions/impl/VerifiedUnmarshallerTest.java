package uk.gov.ida.saml.core.extensions.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.Verified;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class VerifiedUnmarshallerTest {
    @Test
    public void unmarshall_shouldSetValueWhenTrue() throws Exception {
        Verified verified = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:VerifiedType\">\n" +
                "   true" +
                "</saml:AttributeValue>"
        );

        assertThat(verified.getValue()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldSetValueWhenFalse() throws Exception {
        Verified verified = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:VerifiedType\">\n" +
                "   false" +
                "</saml:AttributeValue>"
        );

        assertThat(verified.getValue()).isEqualTo(false);
    }

}
