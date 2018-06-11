package uk.gov.ida.stub.idp.resources;

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
import uk.gov.ida.saml.core.IdaSamlBootstrap;
import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.stub.idp.builders.CountryMetadataBuilder;
import uk.gov.ida.stub.idp.resources.eidas.CountryMetadataResource;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryMetadataResourceTest {

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    private CountryMetadataResource resource;
    private static final String VALID_COUNTRY = "stub-country-one";
    private static final String METADATA_URL_PATTERN = "https://stub.test/{0}/ServiceMetadata";
    private static final String SSO_URL_PATTERN = "https://stub.test/eidas/{0}/SAML2/SSO";
    private EntityDescriptor entityDescriptor;
    private URI validCountryUri;

    @Mock
    private X509Certificate signingCertificate;

    @Mock
    private IdaKeyStore idaKeyStore;

    @Mock
    private CountryMetadataBuilder countryMetadataBuilder;

    @BeforeClass
    public static void classSetUp() {
        IdaSamlBootstrap.bootstrap();
    }

    @Before
    public void setUp() throws CertificateEncodingException, MarshallingException, SecurityException, SignatureException, URISyntaxException {
        validCountryUri = new URI(MessageFormat.format(METADATA_URL_PATTERN, VALID_COUNTRY));
        resource = new CountryMetadataResource(idaKeyStore, METADATA_URL_PATTERN, SSO_URL_PATTERN, countryMetadataBuilder);
        entityDescriptor = (EntityDescriptor) XMLObjectProviderRegistrySupport.getBuilderFactory()
          .getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME).buildObject(EntityDescriptor.DEFAULT_ELEMENT_NAME, EntityDescriptor.TYPE_NAME);
        when(idaKeyStore.getSigningCertificate()).thenReturn(signingCertificate);
        when(countryMetadataBuilder.createEntityDescriptorForProxyNodeService(any(), any(), any(), any())).thenReturn(entityDescriptor);;
    }

    @Test
    public void getShouldReturnADocumentWhenIdpIsKnown() throws URISyntaxException, SecurityException, CertificateEncodingException, SignatureException, MarshallingException {
        final Response response = resource.getMetadata(VALID_COUNTRY);

        URI validCountrySsoUri = new URI(String.format("https://stub.test/eidas/%s/SAML2/SSO", VALID_COUNTRY));
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(Document.class).isAssignableFrom(response.getEntity().getClass());
        verify(countryMetadataBuilder).createEntityDescriptorForProxyNodeService(eq(validCountryUri), eq(validCountrySsoUri), eq(signingCertificate), any());
    }

    @Test
    public void getShouldReturnNotFoundWhenIdpIsNullOrEmpty() throws CertificateEncodingException, MarshallingException, SecurityException, SignatureException {
        Response response = resource.getMetadata(null);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(null);

        response = resource.getMetadata("");
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(null);

        verify(countryMetadataBuilder, times(0)).createEntityDescriptorForProxyNodeService(any(), any(), any(), any());
    }
}
