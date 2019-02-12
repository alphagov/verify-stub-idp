package uk.gov.ida.saml.core.extensions.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.ida.saml.core.Utils;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.RequestedAttributes;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(OpenSAMLRunner.class)
public class RequestedAttributesUnmarshallerTest {

    @Test
    public void shouldUnmarshallAnRequestedAttribute() throws Exception {
        String requestedAttributesString = createdRequestedAttributesString(false);

        RequestedAttributes requestedAttributes = Utils.unmarshall(requestedAttributesString);

        assertThat(requestedAttributes.getOrderedChildren().size()).isEqualTo(5);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(0)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(0)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(1)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(1)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(2)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(2)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(3)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/DateOfBirth");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(3)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(4)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/Gender");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(4)).isRequired()).isEqualTo(false);
    }

    @Test
    public void shouldUnmarshallRequiredWhenAbsent() throws Exception {
        String requestedAttributesString = createdRequestedAttributesString(true);

        RequestedAttributes requestedAttributes = Utils.unmarshall(requestedAttributesString);

        assertThat(requestedAttributes.getOrderedChildren().size()).isEqualTo(5);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(0)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(0)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(1)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(1)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(2)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(2)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(3)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/DateOfBirth");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(3)).isRequired()).isEqualTo(true);
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(4)).getName()).isEqualTo("http://eidas.europa.eu/attributes/naturalperson/Gender");
        assertThat(((RequestedAttribute)requestedAttributes.getOrderedChildren().get(4)).isRequired()).isEqualTo(null);
    }

    private String createdRequestedAttributesString(boolean noRequiredValueForGender) {

        return "<eidas:RequestedAttributes xmlns:eidas=\"http://eidas.europa.eu/saml-extensions\">" +
                "<eidas:RequestedAttribute Name=\"http://eidas.europa.eu/attributes/naturalperson/PersonIdentifier\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" isRequired=\"true\"/>" +
                "<eidas:RequestedAttribute Name=\"http://eidas.europa.eu/attributes/naturalperson/CurrentFamilyName\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" isRequired=\"true\"/>" +
                "<eidas:RequestedAttribute Name=\"http://eidas.europa.eu/attributes/naturalperson/CurrentGivenName\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" isRequired=\"true\"/>" +
                "<eidas:RequestedAttribute Name=\"http://eidas.europa.eu/attributes/naturalperson/DateOfBirth\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" isRequired=\"true\"/>" +
                "<eidas:RequestedAttribute Name=\"http://eidas.europa.eu/attributes/naturalperson/Gender\" NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" "+(noRequiredValueForGender?"":"isRequired=\"false\"")+"/>" +
                "</eidas:RequestedAttributes>";
    }
}
