package uk.gov.ida.stub.idp.resources.singleidp;

import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.views.CancelPreRegistrationPageView;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.RegistrationPageView;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.SINGLE_IDP_PRE_REGISTER_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class SingleIdpPreRegistrationResource {

    private final IdpStubsRepository idpStubsRepository;
    private final CookieFactory cookieFactory;
    private final IdpSessionRepository idpSessionRepository;

    @Inject
    public SingleIdpPreRegistrationResource(
            IdpStubsRepository idpStubsRepository, CookieFactory cookieFactory, IdpSessionRepository idpSessionRepository) {
        this.idpStubsRepository = idpStubsRepository;
        this.cookieFactory = cookieFactory;
        this.idpSessionRepository = idpSessionRepository;
    }

    @GET
    @Path(Urls.SINGLE_IDP_PRE_REGISTER_CANCEL_PATH)
    @Produces(MediaType.TEXT_HTML)
    public Response getPreRegisterCancel(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) Optional<ErrorMessageType> errorMessage) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return Response.ok(new CancelPreRegistrationPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId())).build();
    }

    @GET
    public Response getPreRegister(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) Optional<ErrorMessageType> errorMessage) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        IdpSession session = new IdpSession(
                new SessionId(UUID.randomUUID().toString()));
        final SessionId sessionId = idpSessionRepository.createSession(session);
        idpSessionRepository.updateSession(session.getSessionId(), session.setNewCsrfToken());
        return Response.ok(new RegistrationPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId(), Urls.SINGLE_IDP_PRE_REGISTER_PATH, session.getCsrfToken()))
                .cookie(cookieFactory.createSessionIdCookie(sessionId))
                .build();
    }
}
