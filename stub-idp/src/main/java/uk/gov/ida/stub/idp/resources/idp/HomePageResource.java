package uk.gov.ida.stub.idp.resources.idp;


import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.HomePageView;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.HOMEPAGE_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class HomePageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final IdpSessionRepository sessionRepository;

    @Inject
    public HomePageResource(IdpStubsRepository idpStubsRepository,
                            IdpSessionRepository sessionRepository){

        this.idpStubsRepository = idpStubsRepository;
        this.sessionRepository = sessionRepository;
    }

    @GET
    public Response get(@PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
                        @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
                        @CookieParam(CookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        return Response.ok()
                .entity(new HomePageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId(), getLoggedInUser(sessionCookie)))
                .build();
    }

    private Optional<DatabaseIdpUser> getLoggedInUser(SessionId sessionCookie) {

        Optional<IdpSession> session = Optional.empty();
        Optional<DatabaseIdpUser> loggedInUser = Optional.empty();

        if(sessionCookie != null) {
            session = sessionRepository.get(sessionCookie);
        }
        if(session.isPresent()) {
            loggedInUser = session.get().getIdpUser();
        }
        return loggedInUser;
    }
}
