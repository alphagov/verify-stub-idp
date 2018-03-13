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
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EidasSuccessAuthnResponseServiceTest {

    private EidasSuccessAuthnResponseService service;

    @Mock
    private EidasResponseTransformerProvider eidasResponseTransformerProvider;

    @Mock
    private MetadataRepository metadataRepository;

    private final LocalDate dateOfBirth = new LocalDate(1980, 1, 1);

    @Before
    public void setUp() {
        IdaSamlBootstrap.bootstrap();
        service = new EidasSuccessAuthnResponseService(
            "hubEntityId",
            eidasResponseTransformerProvider,
            Optional.of(metadataRepository),
            "http://stub/{0}/ServiceMetadata"
            );
    }

    @Test
    public void getEidasSuccessResponse() throws URISyntaxException, MarshallingException, SignatureException {
        EidasAuthnRequest request = new EidasAuthnRequest("request-id", "issuer", "destination", "loa", Collections.emptyList());
        Session session = new Session(new SessionId("session-id"), request, "relay-state", Collections.emptyList(), Collections.emptyList(), Optional.empty(), Optional.empty());
        session.setEidasUser(new EidasUser("Firstname", "Familyname", "pid", dateOfBirth, null, null));
        String samlResponseAsString = "some response";
        when(metadataRepository.getAssertionConsumerServiceLocation()).thenReturn(new URI("http://hub.url"));
        when(eidasResponseTransformerProvider.getTransformer()).thenReturn(x -> samlResponseAsString);

        SamlResponseFromValue<Response> samlResponse = service.getEidasSuccessResponse(session, "stub-country");
        Response response = samlResponse.getValue();
        assertThat(response.getIssuer().getValue()).isEqualTo("http://stub/stub-country/ServiceMetadata");
        assertThat(response.getStatus().getStatusCode().getValue()).isEqualTo(StatusCode.SUCCESS);
        assertThat(response.getInResponseTo()).isEqualTo("request-id");
        assertThat(response.getDestination()).isEqualTo("http://hub.url");

        assertThat(response.getAssertions()).hasSize(1);
        assertThat(response.getAssertions().get(0).getAttributeStatements()).hasSize(1);
        assertThatRequiredAssertionsAreIncluded(response.getAssertions().get(0).getAttributeStatements().get(0).getAttributes());

        assertThat(samlResponse.getResponse()).isEqualTo(samlResponseAsString);
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
}
