package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.eidas.CurrentAddress;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class CurrentAddressUnmarshallerTest {
    @Test
    public void shouldUnmarshallCurrentFamilyName() throws Exception {
        final CurrentAddress currentAddress = Utils.unmarshall("" +
                "<saml2:AttributeValue " +
                "   xmlns:eidas-natural=\"http://eidas.europa.eu/attributes/naturalperson\"\n " +
                "   xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n " +
                "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "   xsi:type=\"eidas-natural:CurrentAddressType\">\n" +
                "PGVpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz5DdXJyZW50IEFkZHJlc3M8L2VpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz4K" +
                "</saml2:AttributeValue>"
        );

        assertThat(currentAddress.getCurrentAddress()).isEqualTo("PGVpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz5DdXJyZW50IEFkZHJlc3M8L2VpZGFzLW5hdHVyYWw6RnVsbEN2YWRkcmVzcz4K");
    }
}
