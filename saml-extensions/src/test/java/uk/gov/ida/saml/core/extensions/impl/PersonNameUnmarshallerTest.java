package uk.gov.ida.saml.core.extensions.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class PersonNameUnmarshallerTest {
    @Test
    public void unmarshall_shouldSetValue() throws Exception {
        PersonName personName = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:PersonNameType\"\n" +
                "       ida:Language=\"en-GB\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(personName.getValue()).isEqualTo("John");
    }

    @Test
    public void unmarshall_shouldSetLanguage() throws Exception {
        PersonName personName = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:PersonNameType\"\n" +
                "       ida:Language=\"en-GB\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(personName.getLanguage()).isEqualTo("en-GB");
    }

    @Test
    public void unmarshall_shouldSetVerifiedWhenTrue() throws Exception {
        PersonName personName = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:PersonNameType\"\n" +
                "       ida:Verified=\"true\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(personName.getVerified()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldSetVerifiedWhenFalse() throws Exception {
        PersonName personName = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:PersonNameType\"\n" +
                "       ida:Verified=\"true\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(personName.getVerified()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldSetVerifiedToDefaultWhenAbsent() throws Exception {
        PersonName personName = Utils.unmarshall("" +
                "<saml:AttributeValue " +
                "       xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"\n " +
                "       xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:type=\"ida:PersonNameType\">\n" +
                "   John" +
                "</saml:AttributeValue>"
        );

        assertThat(personName.getVerified()).isEqualTo(false);
    }
}
