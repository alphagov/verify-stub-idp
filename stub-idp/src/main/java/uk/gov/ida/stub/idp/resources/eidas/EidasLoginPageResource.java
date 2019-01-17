package uk.gov.ida.stub.idp.resources.eidas;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.csrf.CSRFCheckProtection;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.exceptions.InvalidEidasSchemeException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.services.EidasAuthnResponseService;
import uk.gov.ida.stub.idp.services.StubCountryService;
import uk.gov.ida.stub.idp.views.EidasLoginPageView;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_SESSION_ID;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_USERNAME_OR_PASSWORD;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.EIDAS_LOGIN_RESOURCE)
@SessionCookieValueMustExistAsASession
@CSRFCheckProtection
public class EidasLoginPageResource {

    private final SessionRepository<EidasSession> sessionRepository;
    private final EidasAuthnResponseService eidasSuccessAuthnResponseRequest;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;
    private final StubCountryRepository stubCountryRepository;
    private final StubCountryService stubCountryService;

    @Inject
    public EidasLoginPageResource(
            SessionRepository<EidasSession> sessionRepository,
            EidasAuthnResponseService eidasSuucessAuthnResponseRequest,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
            StubCountryRepository stubCountryRepository,
            StubCountryService stubCountryService) {
        this.sessionRepository = sessionRepository;
        this.eidasSuccessAuthnResponseRequest = eidasSuucessAuthnResponseRequest;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
        this.stubCountryRepository = stubCountryRepository;
        this.stubCountryService = stubCountryService;
    }

    @GET
    public Response get(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        final Optional<EidasScheme> eidasScheme = EidasScheme.fromString(schemeName);
        if(!eidasScheme.isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        final EidasSession session = checkSession(schemeName, sessionCookie);

        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme.get());

        sessionRepository.updateSession(session.getSessionId(), session.setNewCsrfToken());

        return Response.ok()
                .entity(new EidasLoginPageView(stubCountry.getDisplayName(), stubCountry.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), stubCountry.getAssetId(), session.getCsrfToken()))
                .build();
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @FormParam(Urls.USERNAME_PARAM) String username,
            @FormParam(Urls.PASSWORD_PARAM) String password,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        final Optional<EidasScheme> eidasScheme = EidasScheme.fromString(schemeName);
        if(!eidasScheme.isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        EidasSession session = checkSession(schemeName, sessionCookie);

        try {
            stubCountryService.attachStubCountryToSession(eidasScheme.get(), username, password, session);
        } catch (InvalidUsernameOrPasswordException e) {
            return createErrorResponse(INVALID_USERNAME_OR_PASSWORD, schemeName);
        } catch (InvalidSessionIdException e) {
            return createErrorResponse(INVALID_SESSION_ID, schemeName);
        }

        return Response.seeOther(UriBuilder.fromPath(Urls.EIDAS_CONSENT_RESOURCE)
                .build(schemeName))
                .build();
    }

    @POST
    @Path(Urls.LOGIN_AUTHN_FAILURE_PATH)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postAuthnFailure(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        if(!EidasScheme.fromString(schemeName).isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        EidasSession session = checkAndDeleteAndGetSession(schemeName, sessionCookie);

            final SamlResponse loginFailureResponse = eidasSuccessAuthnResponseRequest.generateAuthnFailed(session, schemeName);
            return samlResponseRedirectViewFactory.sendSamlMessage(loginFailureResponse);
    }

    private EidasSession checkSession(String schemeId, SessionId sessionCookie) {
        if (sessionCookie == null || Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + schemeId), Response.Status.BAD_REQUEST);
        }

        Optional<EidasSession> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new GenericStubIdpException(format("Session is invalid for " + schemeId), Response.Status.BAD_REQUEST);
        }

        return session.get();
    }

    private EidasSession checkAndDeleteAndGetSession(String schemeId, SessionId sessionCookie) {
        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + schemeId), Response.Status.BAD_REQUEST);
        }

        Optional<EidasSession> session = sessionRepository.deleteAndGet(sessionCookie);

        if (!session.isPresent()) {
            throw new GenericStubIdpException(format("Session is invalid for " + schemeId), Response.Status.BAD_REQUEST);
        }
        return session.get();
    }

    private Response createErrorResponse(ErrorMessageType errorMessage, String stubCountry) {
        URI uri = UriBuilder.fromPath(Urls.EIDAS_LOGIN_RESOURCE)
                .queryParam(Urls.ERROR_MESSAGE_PARAM, errorMessage)
                .build(stubCountry);
        return Response.seeOther(uri).build();
    }

}
