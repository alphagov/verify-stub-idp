package uk.gov.ida.stub.idp.resources;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.EidasConsentView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path(Urls.EIDAS_CONSENT_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class EidasConsentResource {

    private final SessionRepository sessionRepository;
    private final SuccessAuthnResponseService successAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Inject
    public EidasConsentResource(
            SessionRepository sessionRepository,
            SuccessAuthnResponseService successAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory) {
        this.successAuthnResponseService = successAuthnResponseService;
        this.sessionRepository = sessionRepository;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
    }

    @GET
    public Response get(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        validateSession(schemeId, sessionCookie);

        return Response.ok(new EidasConsentView("Stub Country", schemeId, schemeId)).build();
    }

    @POST
    public Response consent(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull String submitButtonValue,
            @FormParam(Urls.RANDOMISE_PID_PARAM) boolean randomisePid,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        validateSession(schemeId, sessionCookie);
        Session session = sessionRepository.deleteAndGet(sessionCookie).get();

        SamlResponse samlResponse = successAuthnResponseService.getEidasSuccessResponse(session);
        return samlResponseRedirectViewFactory.sendSamlMessage(samlResponse);
    }

    private void validateSession(String schemeId, SessionId sessionCookie) {
        if (sessionCookie == null || Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw errorResponse("Unable to locate session cookie for " + schemeId);
        }

        Optional<Session> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw errorResponse("Session is invalid for " + schemeId);
        }
    }

    private WebApplicationException errorResponse(String error) {
        return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
    }
}
