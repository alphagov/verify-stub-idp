package uk.gov.ida.stub.idp.resources.idp;

import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.views.CancelPreRegistrationPageView;
import uk.gov.ida.stub.idp.views.ErrorMessageType;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;

import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.CANCEL_PRE_REGISTER_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class CancelPreRegistrationPageResource {

    private final IdpStubsRepository idpStubsRepository;

    @Inject
    public CancelPreRegistrationPageResource(
            IdpStubsRepository idpStubsRepository) {
        this.idpStubsRepository = idpStubsRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) Optional<ErrorMessageType> errorMessage) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return Response.ok(new CancelPreRegistrationPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId())).build();
    }
}
