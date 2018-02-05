package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.EidasAuthnContext;
import uk.gov.ida.saml.core.test.builders.ResponseBuilder;
import uk.gov.ida.stub.idp.builders.EidasResponseBuilder;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasSuccessAuthnResponseServiceTest {

    private EidasSuccessAuthnResponseService service;

    @Mock
    private EidasResponseBuilder eidasResponseBuilder;

    @Mock
    private EidasResponseTransformerProvider eidasResponseTransformerProvider;

    @Mock
    private MetadataRepository metadataRepository;

    @Before
    public void setUp() {
        IdaSamlBootstrap.bootstrap();
        service = new EidasSuccessAuthnResponseService(eidasResponseBuilder, eidasResponseTransformerProvider, Optional.of(metadataRepository), "http://stub/{0}/ServiceMetadata");
    }

    @Test
    public void getEidasSuccessResponse() throws URISyntaxException, MarshallingException, SignatureException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        Session session = new Session(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        session.setEidasUser(new EidasUser("", "", "", null, new LocalDate(1980, 1, 1), Optional.empty()));
        String samlResponseAsString = "some response";
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseBuilder.createEidasResponse(eq("issuer"), eq(StatusCode.SUCCESS), anyString(),
                eq(EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL), any(), eq("request-id"), any(), any(), any(), eq("http://hub.url")))
                .thenReturn(ResponseBuilder.aResponse().build());
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> samlResponseAsString);

        SamlResponse response = service.getEidasSuccessResponse(session, "stub-country");

        assertThat(response.getResponse()).isEqualTo(samlResponseAsString);
    }
}
