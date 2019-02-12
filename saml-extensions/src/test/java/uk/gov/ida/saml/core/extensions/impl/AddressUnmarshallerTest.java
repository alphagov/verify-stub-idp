package uk.gov.ida.saml.core.extensions.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class AddressUnmarshallerTest {
    @Test
    public void unmarshall_shouldUnmarshallAnAddress() throws Exception {
        String line1Value = "1 Cherry Cottage";
        String line2Value = "Wurpel Lane";
        String postCodeValue = "RG99 1YY";
        DateTime fromDateValue = DateTime.parse("1969-01-11", DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC));
        DateTime toDateValue = DateTime.parse("1969-02-11", DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.UTC));
        String internationalPostCodeValue = "RG98 1ZZ";
        String uprn = "672347923456";
        boolean verifiedValue = true;
        String addressXmlString = createAddressXmlString(
                line1Value,
                line2Value,
                internationalPostCodeValue,
                postCodeValue,
                fromDateValue,
                toDateValue,
                verifiedValue,
                uprn);

        Address address = Utils.unmarshall(addressXmlString);

        org.assertj.jodatime.api.Assertions.assertThat(address.getFrom()).isEqualTo(fromDateValue);
        org.assertj.jodatime.api.Assertions.assertThat(address.getTo()).isEqualTo(toDateValue);
        assertThat(address.getVerified()).isEqualTo(verifiedValue);
        List<Line> lines = address.getLines();
        assertThat(lines.size()).isEqualTo(2);
        assertThat(lines.get(0).getValue()).isEqualTo(line1Value);
        assertThat(lines.get(1).getValue()).isEqualTo(line2Value);
        assertThat(address.getPostCode().getValue()).isEqualTo(postCodeValue);
        assertThat(address.getInternationalPostCode().getValue()).isEqualTo(internationalPostCodeValue);
        assertThat(address.getUPRN().getValue()).isEqualTo(uprn);
    }

    @Test
    public void unmarshall_shouldUnmarshallVerifiedWhenTrue() throws Exception {
        String addressXmlString = createAddressXmlString("", "", "", "", DateTime.now(), DateTime.now(), true, "");

        Address address = Utils.unmarshall(addressXmlString);

        assertThat(address.getVerified()).isEqualTo(true);
    }

    @Test
    public void unmarshall_shouldUnmarshallVerifiedWhenFalse() throws Exception {
        String addressXmlString = createAddressXmlString("", "", "", "", DateTime.now(), DateTime.now(), false, "");

        Address address = Utils.unmarshall(addressXmlString);

        assertThat(address.getVerified()).isEqualTo(false);
    }

    @Test
    public void unmarshall_shouldSetVerifiedToDefaultValueWhenAbsent() throws Exception {
        String addressXmlString = "  <saml:AttributeValue" +
                "        xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"" +
                "        xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"" +
                "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "        xsi:type=\"ida:AddressType\">\n" +
                "    <ida:Line>a</ida:Line>\n" +
                "    <ida:Line>a</ida:Line>\n" +
                "  </saml:AttributeValue>";

        Address address = Utils.unmarshall(addressXmlString);

        assertThat(address.getVerified()).isEqualTo(false);
    }

    private String createAddressXmlString(
            String line1Value,
            String line2Value,
            String internationalPostCodeValue,
            String postCodeValue,
            DateTime fromDateValue,
            DateTime toDateValue,
            boolean verifiedValue,
            String uprn) {

        return String.format("" +
                "  <saml:AttributeValue" +
                "        ida:From=\"%s\"" +
                "        ida:To=\"%s\"" +
                "        ida:Verified=\"%b\"" +
                "        xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"" +
                "        xmlns:ida=\"http://www.cabinetoffice.gov.uk/resource-library/ida/attributes\"" +
                "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "        xsi:type=\"ida:AddressType\">\n" +
                "    <ida:Line>%s</ida:Line>\n" +
                "    <ida:Line>%s</ida:Line>\n" +
                "    <ida:PostCode>%s</ida:PostCode>\n" +
                "    <ida:InternationalPostCode>%s</ida:InternationalPostCode>\n" +
                "    <ida:UPRN>%s</ida:UPRN>\n" +
                "  </saml:AttributeValue>",
                fromDateValue.toString("yyyy-MM-dd"),
                toDateValue.toString("yyyy-MM-dd"),
                verifiedValue,
                line1Value,
                line2Value,
                postCodeValue,
                internationalPostCodeValue,
                uprn);
    }
}
