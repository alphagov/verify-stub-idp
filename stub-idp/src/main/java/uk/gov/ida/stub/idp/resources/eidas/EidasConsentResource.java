package uk.gov.ida.stub.idp.resources.eidas;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.csrf.CSRFCheckProtection;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.exceptions.InvalidEidasSchemeException;
import uk.gov.ida.stub.idp.exceptions.InvalidSigningAlgorithmException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.EidasSessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.services.EidasAuthnResponseService;
import uk.gov.ida.stub.idp.views.EidasConsentView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path(Urls.EIDAS_CONSENT_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
@CSRFCheckProtection
public class EidasConsentResource {

    private final EidasSessionRepository sessionRepository;
    private final StubCountryRepository stubCountryRepository;
    private final EidasAuthnResponseService rsaSha256AuthnResponseService;
    private final EidasAuthnResponseService rsaSsaPssAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    private static final String RSASHA_256 = "rsasha256";
    private static final String RSASSA_PSS = "rsassa-pss";

    @Inject
    public EidasConsentResource(
            EidasSessionRepository sessionRepository,
            @Named("RSASHA256EidasAuthnResponseService") EidasAuthnResponseService rsaSha256AuthnResponseService,
            @Named("RSASSAPSSEidasAuthnResponseService") EidasAuthnResponseService rsaSsaPssAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
            StubCountryRepository stubCountryRepository) {
        this.rsaSha256AuthnResponseService = rsaSha256AuthnResponseService;
        this.rsaSsaPssAuthnResponseService = rsaSsaPssAuthnResponseService;
        this.sessionRepository = sessionRepository;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
        this.stubCountryRepository = stubCountryRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        final Optional<EidasScheme> eidasScheme = EidasScheme.fromString(schemeId);
        if(!eidasScheme.isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        EidasSession session = getAndValidateSession(schemeId, sessionCookie, false);

        EidasUser eidasUser = session.getEidasUser().get();
        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme.get());

        sessionRepository.updateSession(session.getSessionId(), session.setNewCsrfToken());

        return Response.ok(new EidasConsentView(stubCountry.getDisplayName(), stubCountry.getFriendlyId(), stubCountry.getAssetId(), eidasUser, session.getCsrfToken())).build();
    }

    @POST
    public Response consent(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @FormParam(Urls.SIGNING_ALGORITHM_PARAM) @NotNull String signingAlgorithm,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull String submitButtonValue,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        if(!EidasScheme.fromString(schemeId).isPresent()) {
            throw new InvalidEidasSchemeException();
        }

        EidasAuthnResponseService successAuthnResponseService;
        if (signingAlgorithm.equals(RSASHA_256)) {
            successAuthnResponseService = rsaSha256AuthnResponseService;
        } else if (signingAlgorithm.equals(RSASSA_PSS)) {
            successAuthnResponseService = rsaSsaPssAuthnResponseService;
        } else {
            throw new InvalidSigningAlgorithmException(signingAlgorithm);
        }

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

    private GenericStubIdpException errorResponse(String error) {
        return new GenericStubIdpException(error, Response.Status.BAD_REQUEST);
    }
}
