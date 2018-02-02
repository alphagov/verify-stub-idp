package uk.gov.ida.stub.idp.resources;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;

@Path(Urls.METADATA_RESOURCE)
@Produces(CountryMetadataResource.SAML_METADATA_MEDIA_TYPE)
public class CountryMetadataResource {
    public final static String SAML_METADATA_MEDIA_TYPE = "application/samlmetadata+xml";
	private final IdpStubsRepository idpStubsRepository;

    @Inject
    public CountryMetadataResource(IdpStubsRepository idpStubsRepository) {
        this.idpStubsRepository = idpStubsRepository;
    }

    @GET
    public Response getMetadata(
        @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName) {
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        return idp.getMetadataLocation()
            .map(this::getMetadataStream)
            .map(Response::ok)
            .orElseGet(() -> Response.status(Response.Status.NOT_FOUND))
            .build();
    }

    private InputStream getMetadataStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException exception) {
            return null;
        }
    }
}