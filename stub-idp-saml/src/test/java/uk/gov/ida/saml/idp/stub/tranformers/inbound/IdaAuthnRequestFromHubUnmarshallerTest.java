package uk.gov.ida.saml.idp.stub.tranformers.inbound;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.saml.idp.stub.transformers.inbound.IdaAuthnRequestFromHubUnmarshaller;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration.EXACT;
import static uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_1;
import static uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_2;

@RunWith(OpenSAMLMockitoRunner.class)
public class IdaAuthnRequestFromHubUnmarshallerTest {

    @Mock
    private AuthnRequest authnRequest;
    @Mock
    private RequestedAuthnContext requestedAuthnContext;
    @Mock
    private AuthnContextClassRef authnContextClassRef;
    @Mock
    private Conditions conditions;
    @Mock
    private Issuer issuer;

    @Before
    public void setupAuthnRequest() {
        when(authnRequest.getIssuer()).thenReturn(issuer);
        when(authnRequest.getRequestedAuthnContext()).thenReturn(requestedAuthnContext);
        when(authnRequest.getConditions()).thenReturn(conditions);
        when(requestedAuthnContext.getAuthnContextClassRefs()).thenReturn(Arrays.asList(authnContextClassRef, authnContextClassRef));
        when(requestedAuthnContext.getComparison()).thenReturn(EXACT);
        when(authnContextClassRef.getAuthnContextClassRef()).thenReturn(IdaAuthnContext.LEVEL_2_AUTHN_CTX, IdaAuthnContext.LEVEL_1_AUTHN_CTX);
    }

    @Test
    public void shouldMapLevelOfAssurance() throws Exception {
        IdaAuthnRequestFromHubUnmarshaller unmarshaller = new IdaAuthnRequestFromHubUnmarshaller();

        IdaAuthnRequestFromHub outputIdaAuthnRequestFromHub = unmarshaller.fromSaml(authnRequest);

        assertThat(outputIdaAuthnRequestFromHub.getLevelsOfAssurance()).isEqualTo(Arrays.asList(LEVEL_1, LEVEL_2));
    }

    @Test
    public void shouldKeepComparisonType() throws Exception {
        IdaAuthnRequestFromHubUnmarshaller unmarshaller = new IdaAuthnRequestFromHubUnmarshaller();

        IdaAuthnRequestFromHub outputIdaAuthnRequestFromHub = unmarshaller.fromSaml(authnRequest);

        assertThat(outputIdaAuthnRequestFromHub.getComparisonType()).isEqualTo(EXACT);
    }
}
