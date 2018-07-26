package uk.gov.ida.stub.idp.resources.idp;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.ConsentView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path(Urls.CONSENT_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class ConsentResource {

    private final IdpStubsRepository idpStubsRepository;
    private final SessionRepository<IdpSession> sessionRepository;
    private final SuccessAuthnResponseService successAuthnResponseService;
    private final NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    public static final String I_AGREE_SUBMIT_VALUE = "I Agree";
    public static final String I_REFUSE_SUBMIT_VALUE = "I Refuse";

    @Inject
    public ConsentResource(
            IdpStubsRepository idpStubsRepository,
            SessionRepository<IdpSession> sessionRepository,
            SuccessAuthnResponseService successAuthnResponseService,
            NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory) {
        this.successAuthnResponseService = successAuthnResponseService;
        this.idpStubsRepository = idpStubsRepository;
        this.sessionRepository = sessionRepository;
        this.nonSuccessAuthnResponseService = nonSuccessAuthnResponseService;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
    }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        IdpSession session = getAndValidateSession(idpName, sessionCookie);

        DatabaseIdpUser idpUser = session.getIdpUser().get();

        List<AuthnContext> requestLevelsOfAssurance = session.getIdaAuthnRequestFromHub().getLevelsOfAssurance();
        AuthnContext userLevelOfAssurance = idpUser.getLevelOfAssurance();
        boolean isUserLOATooLow = !requestLevelsOfAssurance.stream().anyMatch(loa -> loa.equals(userLevelOfAssurance));

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return Response.ok(new ConsentView(idp.getDisplayName(), idp.getFriendlyId(), idp.getAssetId(), idpUser, isUserLOATooLow, userLevelOfAssurance, requestLevelsOfAssurance)).build();
    }

    private WebApplicationException errorResponse(String error) {
        return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
    }

    @POST
    public Response consent(
            @Context HttpServletRequest httpServletRequest,
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull String submitButtonValue,
            @FormParam(Urls.RANDOMISE_PID_PARAM) boolean randomisePid,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {
        
        IdpSession session = getAndValidateSession(idpName, sessionCookie);
        sessionRepository.deleteSession(sessionCookie);

        switch (submitButtonValue) {
            case I_AGREE_SUBMIT_VALUE:
                return samlResponseRedirectViewFactory.sendSamlMessage(successAuthnResponseService.getSuccessResponse(randomisePid, httpServletRequest.getRemoteAddr(), idpName, session));
            case I_REFUSE_SUBMIT_VALUE:
                return samlResponseRedirectViewFactory.sendSamlMessage(nonSuccessAuthnResponseService.generateNoAuthnContext(session.getIdaAuthnRequestFromHub().getId(), idpName, session.getRelayState()));

            default:
                throw errorResponse("Invalid button value " + submitButtonValue);
        }
    }

    private IdpSession getAndValidateSession(String idpName, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw errorResponse("Unable to locate session cookie for " + idpName);
        }

        Optional<IdpSession> session = sessionRepository.get(sessionCookie);
        
        if (!session.isPresent() || !session.get().getIdpUser().isPresent() || session.get().getIdaAuthnRequestFromHub() == null) {
            throw errorResponse("Session is invalid for " + idpName);
        }
        
        return session.get();
    }
}
