package uk.gov.ida.stub.idp.domain;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.SPType;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributeBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesImpl;
import uk.gov.ida.saml.core.extensions.impl.SPTypeBuilder;
import uk.gov.ida.saml.core.test.builders.AuthnContextClassRefBuilder;
import uk.gov.ida.saml.core.test.builders.AuthnRequestBuilder;
import uk.gov.ida.saml.core.test.builders.IssuerBuilder;
import uk.gov.ida.saml.hub.domain.LevelOfAssurance;

import static org.assertj.core.api.Assertions.assertThat;

public class EidasAuthnRequestTest {

    @Before
    public void setUp(){
        IdaSamlBootstrap.bootstrap();
    }

    @Test
    public void shouldConvertAuthnRequestToEidasAuthnRequest() {
        AuthnRequest authnRequest = AuthnRequestBuilder.anAuthnRequest()
                .withIssuer(
                        IssuerBuilder.anIssuer().withIssuerId("issuer-id").build()
                )
                .withId("request-id")
                .withDestination("Destination")
                .build();

        RequestedAuthnContext requestedAuthnContext = new RequestedAuthnContextBuilder().buildObject();

        AuthnContextClassRef authnContextClassRef = AuthnContextClassRefBuilder.anAuthnContextClassRef()
                .withAuthnContextClasRefValue(LevelOfAssurance.SUBSTANTIAL.toString())
                .build();

        requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);
        authnRequest.setRequestedAuthnContext(requestedAuthnContext);

        authnRequest.setExtensions(createEidasExtensions());

        EidasAuthnRequest actualEidasAuthnRequest = EidasAuthnRequest.buildFromAuthnRequest(authnRequest);

        assertThat(actualEidasAuthnRequest.getRequestId()).isEqualTo("request-id");
        assertThat(actualEidasAuthnRequest.getIssuer()).isEqualTo("issuer-id");
        assertThat(actualEidasAuthnRequest.getDestination()).isEqualTo("Destination");
        assertThat(actualEidasAuthnRequest.getRequestedLoa()).isEqualTo("http://eidas.europa.eu/LoA/substantial");

        assertThat(actualEidasAuthnRequest.getAttributes().size()).isEqualTo(1);
        uk.gov.ida.stub.idp.domain.RequestedAttribute requestedAttribute = actualEidasAuthnRequest.getAttributes().get(0);
        assertThat(requestedAttribute.getName()).isEqualTo(IdaConstants.Eidas_Attributes.FamilyName.NAME);
        assertThat(requestedAttribute.isRequired()).isEqualTo(true);
    }

    // this code is copied from EidasAuthnRequestBuilder
    private Extensions createEidasExtensions() {
        SPType spType = new SPTypeBuilder().buildObject();
        spType.setValue("public");

        RequestedAttributesImpl requestedAttributes = (RequestedAttributesImpl)new RequestedAttributesBuilder().buildObject();
        requestedAttributes.setRequestedAttributes(createRequestedAttribute(IdaConstants.Eidas_Attributes.FamilyName.NAME));

        Extensions extensions = new ExtensionsBuilder().buildObject();
        extensions.getUnknownXMLObjects().add(spType);
        extensions.getUnknownXMLObjects().add(requestedAttributes);
        return extensions;
    }

    private RequestedAttribute createRequestedAttribute(String requestedAttributeName) {
        RequestedAttribute attr = new RequestedAttributeBuilder().buildObject();
        attr.setName(requestedAttributeName);
        attr.setNameFormat(Attribute.URI_REFERENCE);
        attr.setIsRequired(true);
        return attr;
    }

}
