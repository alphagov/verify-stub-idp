package uk.gov.ida.stub.idp.services;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.core.extensions.eidas.CurrentFamilyName;
import uk.gov.ida.saml.core.extensions.eidas.CurrentGivenName;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;
import uk.gov.ida.saml.core.extensions.eidas.PersonIdentifier;
import uk.gov.ida.stub.idp.domain.EidasAuthnRequest;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponseFromValue;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasAuthnResponseServiceTest {
    private final String SCHEME_ID = "stub-country";
    private final String SAML_RESPONSE_AS_STRING = "some response";

    private EidasAuthnResponseService service;

    @Mock
    private EidasResponseTransformerProvider eidasResponseTransformerProvider;

    @Mock
    private MetadataRepository metadataRepository;

    private final LocalDate dateOfBirth = new LocalDate(1980, 1, 1);

    @Before
    public void setUp() {
        IdaSamlBootstrap.bootstrap();
        service = new EidasAuthnResponseService(
            "hubEntityId",
            eidasResponseTransformerProvider,
            Optional.of(metadataRepository),
            "http://stub/{0}/ServiceMetadata"
            );
    }

    @Test
    public void getEidasSuccessResponse() throws URISyntaxException, MarshallingException, SignatureException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        EidasSession session = new EidasSession(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        session.setEidasUser(new EidasUser("Firstname", Optional.empty(), "Familyname", Optional.empty(), "pid", dateOfBirth, null, null));
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> SAML_RESPONSE_AS_STRING);

        SamlResponseFromValue<Response> samlResponse = service.getSuccessResponse(session, SCHEME_ID);
        Response response = samlResponse.getResponseObject();
        assertThat(response.getIssuer().getValue()).isEqualTo("http://stub/stub-country/ServiceMetadata");
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo(StatusCode.SUCCESS);
        assertThat(response.getInResponseTo()).isEqualTo("request-id");
        assertThat(response.getDestination()).isEqualTo("http://hub.url");

        assertThat(response.getAssertions()).hasSize(1);
        assertThat(response.getAssertions().get(0).getAttributeStatements()).hasSize(1);
        assertThatRequiredAssertionsAreIncluded(response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes());

        assertThat(samlResponse.getResponseString()).isEqualTo(SAML_RESPONSE_AS_STRING);
    }

    @Test
    public void getEidasSuccessWithNonLatinNamesDataResponse() throws URISyntaxException, MarshallingException, SignatureException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        EidasSession session = new EidasSession(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        session.setEidasUser(new EidasUser("Firstname", Optional.of("nonLatinFirstname"), "Familyname", Optional.of("nonLatinFamilyname"), "pid", dateOfBirth, null, null));
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> SAML_RESPONSE_AS_STRING);

        SamlResponseFromValue<Response> samlResponse = service.getSuccessResponse(session, SCHEME_ID);
        Response response = samlResponse.getResponseObject();
        assertThat(response.getIssuer().getValue()).isEqualTo("http://stub/stub-country/ServiceMetadata");
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo(StatusCode.SUCCESS);
        assertThat(response.getInResponseTo()).isEqualTo("request-id");
        assertThat(response.getDestination()).isEqualTo("http://hub.url");

        assertThat(response.getAssertions()).hasSize(1);
        assertThat(response.getAssertions().get(0).getAttributeStatements()).hasSize(1);
        assertThatRequiredAssertionsAreIncluded(response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes());
        assertThatNonLatinAssertionsAreIncluded(response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes());

        assertThat(samlResponse.getResponseString()).isEqualTo(SAML_RESPONSE_AS_STRING);
    }

    @Test
    public void getAuthnFailResponse() throws URISyntaxException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        EidasSession session = new EidasSession(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> SAML_RESPONSE_AS_STRING);

        SamlResponseFromValue<Response> samlResponse = service.generateAuthnFailed(session, SCHEME_ID);
        Response response = samlResponse.getResponseObject();
        assertThat(response.getIssuer().getValue()).isEqualTo("http://stub/stub-country/ServiceMetadata");
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo(StatusCode.RESPONDER);
        assertThat(response.getStatus().getStatusCode().getStatusCode().getValue()).isEqualTo(StatusCode.AUTHN_FAILED);
        assertThat(response.getInResponseTo()).isEqualTo("request-id");
        assertThat(response.getDestination()).isEqualTo("http://hub.url");

        assertThat(samlResponse.getResponseString()).isEqualTo(SAML_RESPONSE_AS_STRING);
    }

    private void assertThatRequiredAssertionsAreIncluded(List<Attribute> attributes) {
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

    private void assertThatNonLatinAssertionsAreIncluded(List<Attribute> attributes) {
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.FirstName.NAME)
                && ((CurrentGivenName) a.getAttributeValues().get(1)).getFirstName().equals("nonLatinFirstname") &&
                !((CurrentGivenName) a.getAttributeValues().get(1)).isLatinScript()
        )).isTrue();
        assertThat(attributes.stream().anyMatch(a -> a.getName().equals(IdaConstants.Eidas_Attributes.FamilyName.NAME)
                && ((CurrentFamilyName) a.getAttributeValues().get(1)).getFamilyName().equals("nonLatinFamilyname") &&
                !((CurrentFamilyName) a.getAttributeValues().get(1)).isLatinScript()
        )).isTrue();
    }
}
