package uk.gov.ida.stub.idp.resources;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.w3c.dom.Document;

import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.stub.idp.builders.CountryMetadataBuilder;
import uk.gov.ida.stub.idp.exceptions.IdpNotFoundException;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

@RunWith(MockitoJUnitRunner.class)
public class CountryMetadataResourceTest {

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private CountryMetadataResource resource;
    private static final String VALID_IDP = "stub-country-one";
    private static final String INVALID_IDP = "stub-country-two";
    private EntityDescriptor entityDescriptor;
    private URI validIdpUri;
    private URI invalidIdpUri;

    @Mock
    private Idp validIdp;

    @Mock
    private X509Certificate signingCertificate;

    @Mock
    private IdpStubsRepository idpStubsRepository;

    @Mock
    private IdaKeyStore idaKeyStore;

    @Mock
    private CountryMetadataBuilder countryMetadataBuilder;

    @Before
    public void setUp() throws CertificateEncodingException, MarshallingException, SecurityException, SignatureException, URISyntaxException {
        validIdpUri = new URI(String.format("https://stub.test/%s/ServiceMetadata", VALID_IDP));
        invalidIdpUri = new URI(String.format("https://stub.test/%s/ServiceMetadata", INVALID_IDP));
        resource = new CountryMetadataResource(idpStubsRepository, idaKeyStore, countryMetadataBuilder);
        entityDescriptor = (EntityDescriptor) XMLObjectProviderRegistrySupport.getBuilderFactory()
          .getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME).buildObject(EntityDescriptor.DEFAULT_ELEMENT_NAME, EntityDescriptor.TYPE_NAME);
        when(idpStubsRepository.getIdpWithFriendlyId(VALID_IDP)).thenReturn(validIdp);
        when(idpStubsRepository.getIdpWithFriendlyId(INVALID_IDP)).thenThrow(IdpNotFoundException.class);
        when(idaKeyStore.getSigningCertificate()).thenReturn(signingCertificate);
        when(countryMetadataBuilder.createEntityDescriptorForProxyNodeService(any(), any(), any())).thenReturn(entityDescriptor);;
    }

    @Test
    public void getShouldReturnADocumentWhenIdpIsKnown(){
        final UriInfo requestContext = mock(UriInfo.class);
        when(requestContext.getAbsolutePath()).thenReturn(validIdpUri);

        final Response response = resource.getMetadata(requestContext, VALID_IDP);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(Document.class).isAssignableFrom(response.getEntity().getClass());
    }

    @Test
    public void getShouldReturnNotFoundWhenIdpIsUnknown() throws CertificateEncodingException, MarshallingException, SecurityException, SignatureException {
        final UriInfo requestContext = mock(UriInfo.class);
        when(requestContext.getAbsolutePath()).thenReturn(invalidIdpUri);

        final Response response = resource.getMetadata(requestContext, INVALID_IDP);

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(null);
        verify(countryMetadataBuilder, times(0)).createEntityDescriptorForProxyNodeService(any(), any(), any());
    }
}
