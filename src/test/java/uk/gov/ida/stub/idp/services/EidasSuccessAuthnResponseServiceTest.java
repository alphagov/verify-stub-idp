package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.EidasAuthnContext;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
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

    @Captor
    private ArgumentCaptor<List<Attribute>> attributesCaptor;

    private final LocalDate dateOfBirth = new LocalDate(1980, 1, 1);

    @Before
    public void setUp() {
        IdaSamlBootstrap.bootstrap();
        service = new EidasSuccessAuthnResponseService(eidasResponseBuilder, eidasResponseTransformerProvider, Optional.of(metadataRepository), "http://stub/{0}/ServiceMetadata");
    }

    @Test
    public void getEidasSuccessResponse() throws URISyntaxException, MarshallingException, SignatureException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        Session session = new Session(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        session.setEidasUser(new EidasUser("Firstname", "Familyname", "pid", null, dateOfBirth, Optional.empty()));
        String samlResponseAsString = "some response";
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> samlResponseAsString);

        SamlResponse response = service.getEidasSuccessResponse(session, "stub-country");

        verify(eidasResponseBuilder).createEidasResponse(eq("http://stub/stub-country/ServiceMetadata"), eq(StatusCode.SUCCESS), anyString(),
                eq(EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL), attributesCaptor.capture(), eq("request-id"), any(), any(), any(), eq("http://hub.url"));
        assertThatRequiredAssertionsAreIncluded();
        assertThat(response.getResponse()).isEqualTo(samlResponseAsString);
    }

    private void assertThatRequiredAssertionsAreIncluded() {
        List<Attribute> attributes = attributesCaptor.getValue();
        assertThat(attributes).hasSize(4);
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.FirstName.NAME)
                        && ((CurrentGivenName) a.getAttributeValues().get(0)).getFirstName().equals("Firstname")))
                .isTrue();
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.FamilyName.NAME)
                        && ((CurrentFamilyName) a.getAttributeValues().get(0)).getFamilyName().equals("Familyname")))
                .isTrue();
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.PersonIdentifier.NAME)
                        && ((PersonIdentifier) a.getAttributeValues().get(0)).getPersonIdentifier().equals("pid")))
                .isTrue();
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.DateOfBirth.NAME)
                        && ((DateOfBirth) a.getAttributeValues().get(0)).getDateOfBirth().equals(dateOfBirth)))
                .isTrue();

    }
}
