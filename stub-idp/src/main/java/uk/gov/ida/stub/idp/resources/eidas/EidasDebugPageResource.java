package uk.gov.ida.stub.idp.resources.eidas;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.EidasScheme;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.exceptions.InvalidEidasSchemeException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.EidasSession;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.repositories.StubCountry;
import uk.gov.ida.stub.idp.repositories.StubCountryRepository;
import uk.gov.ida.stub.idp.views.EidasDebugPageView;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Path(Urls.EIDAS_DEBUG_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class EidasDebugPageResource {

    private final SessionRepository<EidasSession> sessionRepository;
    private final StubCountryRepository stubCountryRepository;

    @Inject
    public EidasDebugPageResource(
            SessionRepository<EidasSession> sessionRepository,
            StubCountryRepository stubCountryRepository) {
        this.sessionRepository = sessionRepository;
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

        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format(("Unable to locate session cookie for " + schemeId)), Response.Status.BAD_REQUEST);
        }

        Optional<EidasSession> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new GenericStubIdpException(format(("Session is invalid for " + schemeId)), Response.Status.BAD_REQUEST);
        }

        StubCountry stubCountry = stubCountryRepository.getStubCountryWithFriendlyId(eidasScheme.get());
        return Response.ok(new EidasDebugPageView(stubCountry.getDisplayName(), stubCountry.getFriendlyId(), stubCountry.getAssetId(), session.get())).build();
    }

}
