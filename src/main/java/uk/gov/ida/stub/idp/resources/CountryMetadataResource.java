package uk.gov.ida.stub.idp.resources;
import java.net.URI;
import java.security.cert.CertificateEncodingException;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import uk.gov.ida.saml.security.IdaKeyStore;
import uk.gov.ida.saml.serializers.XmlObjectToElementTransformer;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.builders.CountryMetadataBuilder;

@Path(Urls.METADATA_RESOURCE)
@Produces(CountryMetadataResource.SAML_METADATA_MEDIA_TYPE)
public class CountryMetadataResource {
    public static final String SAML_METADATA_MEDIA_TYPE = "application/samlmetadata+xml";

    private static final Logger LOG = LoggerFactory.getLogger(CountryMetadataResource.class);
    private final IdaKeyStore idaKeyStore;
	private final CountryMetadataBuilder countryMetadataBuilder;

	@Inject
    public CountryMetadataResource(IdaKeyStore idaKeyStore, CountryMetadataBuilder countryMetadataBuilder) {
        this.countryMetadataBuilder = countryMetadataBuilder;
        this.idaKeyStore = idaKeyStore;
    }

    @GET
    public Response getMetadata(
        @Context UriInfo uriInfo,
        @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName) {

        if (idpName == null || idpName.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        try {
            Document metadata = getMetadataDocument(uriInfo.getAbsolutePath());
            return Response.ok(metadata).build();
		} catch (CertificateEncodingException | MarshallingException | SecurityException | SignatureException e) {
            LOG.error("Failed to generate metadata", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Document getMetadataDocument(URI requestPath) throws CertificateEncodingException, MarshallingException, SecurityException, SignatureException {
        EntityDescriptor metadata = countryMetadataBuilder.createEntityDescriptorForProxyNodeService(
            requestPath,
            idaKeyStore.getSigningCertificate(),
            /* We use signing cert for encryption cert below because we need some certificate to be there
               but the stub currently doesn't have or need an encrypting certificate. */
            idaKeyStore.getSigningCertificate());
        XmlObjectToElementTransformer<EntityDescriptor> transformer = new XmlObjectToElementTransformer<>();
        return transformer.apply(metadata).getOwnerDocument();
    }
}