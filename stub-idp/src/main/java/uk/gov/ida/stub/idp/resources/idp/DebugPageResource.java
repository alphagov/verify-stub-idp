package uk.gov.ida.stub.idp.resources.idp;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.views.DebugPageView;

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

@Path(Urls.DEBUG_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class DebugPageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final IdpSessionRepository sessionRepository;

    @Inject
    public DebugPageResource(
            IdpStubsRepository idpStubsRepository,
            IdpSessionRepository sessionRepository) {
        this.idpStubsRepository = idpStubsRepository;
        this.sessionRepository = sessionRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + idpName), Response.Status.BAD_REQUEST);
        }

        Optional<IdpSession> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new GenericStubIdpException(format("Session is invalid for " + idpName), Response.Status.BAD_REQUEST);
        }

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return Response.ok(new DebugPageView(idp.getDisplayName(), idp.getFriendlyId(), idp.getAssetId(), session.get())).build();
    }

}
