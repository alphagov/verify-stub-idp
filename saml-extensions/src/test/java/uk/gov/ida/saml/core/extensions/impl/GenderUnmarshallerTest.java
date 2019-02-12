package uk.gov.ida.saml.core.extensions.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.Gender;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class GenderUnmarshallerTest {
    @Test
    public void unmarshall_shouldSetValue() throws Exception {
        Gender gender = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:GenderType\"\n" +
                "       ida:Language=\"en-GB\">\n" +
                "   Female" +
                "</saml:AttributeValue>"
        );

        assertThat(gender.getValue()).isEqualTo("Female");
    }

    @Test
    public void unmarshall_shouldSetVerifiedWhenTrue() throws Exception {
        Gender gender = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:GenderType\"\n" +
                "       ida:Verified=\"true\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(gender.getVerified()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldSetVerifiedWhenFalse() throws Exception {
        Gender gender = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:GenderType\"\n" +
                "       ida:Verified=\"true\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(gender.getVerified()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldSetVerifiedToDefaultWhenAbsent() throws Exception {
        Gender gender = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:GenderType\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(gender.getVerified()).isEqualTo(false);
    }
}
