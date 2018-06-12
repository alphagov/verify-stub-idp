package uk.gov.ida.stub.idp.domain;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.ExtensionsBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentFamilyNameBuilder;
import uk.gov.ida.saml.core.extensions.eidas.impl.CurrentFamilyNameImpl;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributeBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesBuilder;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesImpl;
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

        CurrentFamilyName currentFamilyName = new CurrentFamilyNameBuilder().buildObject();
        currentFamilyName.setFamilyName("family-name");

        authnRequest.setExtensions(anExtensionWithCurrentFamilyName(currentFamilyName));

        EidasAuthnRequest actualEidasAuthnRequest = EidasAuthnRequest.buildFromAuthnRequest(authnRequest);

        assertThat(actualEidasAuthnRequest.getRequestId()).isEqualTo("request-id");
        assertThat(actualEidasAuthnRequest.getIssuer()).isEqualTo("issuer-id");
        assertThat(actualEidasAuthnRequest.getDestination()).isEqualTo("Destination");
        assertThat(actualEidasAuthnRequest.getRequestedLoa()).isEqualTo("http://eidas.europa.eu/LoA/substantial");

//        XMLObject xmlObject = actualEidasAuthnRequest.getAttributes().get(0).getAttributeValues().get(0);
//        assertThat(xmlObject.getClass()).isEqualTo(CurrentFamilyNameImpl.class);
//        assertThat(((CurrentFamilyNameImpl) xmlObject).getFamilyName()).isEqualTo(currentFamilyName.getFamilyName());
    }

    private Extensions anExtensionWithCurrentFamilyName(CurrentFamilyName currentFamilyName) {
        RequestedAttribute requestedAttribute = new RequestedAttributeBuilder().buildObject();
        requestedAttribute.getAttributeValues().add(currentFamilyName);

        RequestedAttributesImpl requestedAttributes = (RequestedAttributesImpl) new RequestedAttributesBuilder().buildObject();
        requestedAttributes.setRequestedAttributes(requestedAttribute);

        Extensions extensions = new ExtensionsBuilder().buildObject();
        extensions.getUnknownXMLObjects().add(requestedAttributes);

        return extensions;
    }
}
