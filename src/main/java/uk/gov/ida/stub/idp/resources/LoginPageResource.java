package uk.gov.ida.stub.idp.resources;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.FraudIndicator;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.domain.SubmitButtonValue;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.LoginPageView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_SESSION_ID;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_USERNAME_OR_PASSWORD;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.LOGIN_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class LoginPageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;
    private final IdpUserService idpUserService;
    private final SessionRepository sessionRepository;

    @Inject
    public LoginPageResource(
            IdpStubsRepository idpStubsRepository,
            NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
            IdpUserService idpUserService,
            SessionRepository sessionRepository) {
        this.nonSuccessAuthnResponseService = nonSuccessAuthnResponseService;
        this.idpStubsRepository = idpStubsRepository;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
        this.idpUserService = idpUserService;
        this.sessionRepository = sessionRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        checkAndGetSession(idpName, sessionCookie);

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        return Response.ok()
                .entity(new LoginPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId()))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.USERNAME_PARAM) String username,
            @FormParam(Urls.PASSWORD_PARAM) String password,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull SubmitButtonValue submitButtonValue,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndGetSession(idpName, sessionCookie);
        final String samlRequestId = session.getIdaAuthnRequestFromHub().getId();

        switch (submitButtonValue) {
            case Cancel: {

                session = checkAndDeleteAndGetSession(idpName, sessionCookie);

                final SamlResponse cancelResponse = nonSuccessAuthnResponseService.generateAuthnCancel(idpName, samlRequestId, session.getRelayState());
                return samlResponseRedirectViewFactory.sendSamlMessage(cancelResponse);
            }
            case SignIn:
                try {
                    idpUserService.attachIdpUserToSession(idpName, username, password, sessionCookie);
                } catch (InvalidUsernameOrPasswordException e) {
                    return createErrorResponse(INVALID_USERNAME_OR_PASSWORD, idpName);
                } catch (InvalidSessionIdException e) {
                    return createErrorResponse(INVALID_SESSION_ID, idpName);
                }
                return Response.seeOther(UriBuilder.fromPath(Urls.CONSENT_RESOURCE)
                        .build(idpName))
                        .build();
            default:
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @POST
    @Path(Urls.LOGIN_AUTHN_FAILURE_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postAuthnLoginFailure(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse loginFailureResponse = nonSuccessAuthnResponseService.generateAuthnFailed(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlResponseRedirectViewFactory.sendSamlMessage(loginFailureResponse);
    }

    @POST
    @Path(Urls.LOGIN_NO_AUTHN_CONTEXT_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postNoAuthnContext(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse noAuthnResponse = nonSuccessAuthnResponseService.generateNoAuthnContext(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlResponseRedirectViewFactory.sendSamlMessage(noAuthnResponse);
    }

    @POST
    @Path(Urls.LOGIN_UPLIFT_FAILED_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postUpliftFailed(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse upliftFailedResponse = nonSuccessAuthnResponseService.generateUpliftFailed(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlResponseRedirectViewFactory.sendSamlMessage(upliftFailedResponse);
    }


    @POST
    @Path(Urls.LOGIN_FRAUD_FAILURE_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postLoginFraudAuthnFailure(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.LOGIN_FAILURE_STATUS_PARAM) @NotNull FraudIndicator fraudIndicatorParam,
            @Context HttpServletRequest httpServletRequest,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        final String clientIpAddress = httpServletRequest.getRemoteHost();

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse fraudResponse = nonSuccessAuthnResponseService.generateFraudResponse(idpName, session.getIdaAuthnRequestFromHub().getId(), fraudIndicatorParam, clientIpAddress, session);
        return samlResponseRedirectViewFactory.sendSamlMessage(fraudResponse);
    }

    @POST
    @Path(Urls.LOGIN_REQUESTER_ERROR_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postRequesterError(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.REQUESTER_ERROR_MESSAGE_PARAM) String requesterErrorMessage,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse requesterErrorResponseFromIdp = nonSuccessAuthnResponseService.generateRequesterError(session.getIdaAuthnRequestFromHub().getId(), requesterErrorMessage, idpName, session.getRelayState());
        return samlResponseRedirectViewFactory.sendSamlMessage(requesterErrorResponseFromIdp);
    }

    @POST
    @Path(Urls.LOGIN_AUTHN_PENDING_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postAuthnPending(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkAndDeleteAndGetSession(idpName, sessionCookie);

        final SamlResponse pendingResponse = nonSuccessAuthnResponseService.generateAuthnPending(idpName, session.getIdaAuthnRequestFromHub().getId(), session.getRelayState());
        return samlResponseRedirectViewFactory.sendSamlMessage(pendingResponse);
    }

    private Session checkAndGetSession(String idpName, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Unable to locate session cookie for " + idpName))).build());
        }

        Optional<Session> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Session is invalid for " + idpName))).build());
        }
        return session.get();
    }

    private Session checkAndDeleteAndGetSession(String idpName, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Unable to locate session cookie for " + idpName))).build());
        }

        Optional<Session> session = sessionRepository.deleteAndGet(sessionCookie);

        if (!session.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Session is invalid for " + idpName))).build());
        }
        return session.get();
    }

    private Response createErrorResponse(ErrorMessageType errorMessage, String idpName) {
        URI uri = UriBuilder.fromPath(Urls.LOGIN_RESOURCE)
                .queryParam(Urls.ERROR_MESSAGE_PARAM, errorMessage)
                .build(idpName);
        return Response.seeOther(uri).build();
    }
}
