package uk.gov.ida.stub.idp.resources;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.services.EidasAuthnResponseService;
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

    private final SessionRepository<EidasSession> sessionRepository;
    private final StubCountryRepository stubCountryRepository;
    private final EidasAuthnResponseService successAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Inject
    public EidasConsentResource(
            SessionRepository<EidasSession> sessionRepository,
            EidasAuthnResponseService successAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
            StubCountryRepository stubCountryRepository) {
        this.successAuthnResponseService = successAuthnResponseService;
        this.sessionRepository = sessionRepository;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
        this.stubCountryRepository = stubCountryRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

		EidasSession session = getAndValidateSession(schemeId, sessionCookie, false);

        EidasUser eidasUser = session.getEidasUser().get();
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(schemeId);

        return Response.ok(new EidasConsentView(stubCountry.getDisplayName(), stubCountry.getFriendlyId(), stubCountry.getAssetId(), eidasUser)).build();
    }

    @POST
    public Response consent(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull String submitButtonValue,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        EidasSession session = getAndValidateSession(schemeId, sessionCookie, true);

        SamlResponse samlResponse = successAuthnResponseService.getSuccessResponse(session, schemeId);
        return samlResponseRedirectViewFactory.sendSamlMessage(samlResponse);
    }

    private EidasSession getAndValidateSession(String schemeId, SessionId sessionCookie, boolean shouldDelete) {
        if (sessionCookie == null || Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw errorResponse("Unable to locate session cookie for " + schemeId);
        }

        Optional<EidasSession> session = shouldDelete ? sessionRepository.deleteAndGet(sessionCookie) : sessionRepository.get(sessionCookie);

        if (!session.isPresent() || !session.get().getEidasUser().isPresent() || session.get().getEidasAuthnRequest() == null) {
            throw errorResponse("Session is invalid for " + schemeId);
        }

        return session.get();
    }

    private WebApplicationException errorResponse(String error) {
        return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(error).build());
    }
}
