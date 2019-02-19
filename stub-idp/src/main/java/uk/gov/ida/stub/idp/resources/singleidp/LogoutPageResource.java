package uk.gov.ida.stub.idp.resources.singleidp;

import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.cookies.HttpOnlyNewCookie;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path(Urls.SINGLE_IDP_LOGOUT_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class LogoutPageResource {

    private IdpSessionRepository idpSessionRepository;

    @Inject
    public LogoutPageResource(IdpSessionRepository idpSessionRepository) {
        this.idpSessionRepository = idpSessionRepository;
    }

    @GET
    public Response get(@PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
                        @CookieParam(CookieNames.SESSION_COOKIE_NAME) SessionId session) {

        idpSessionRepository.deleteSession(session);
        return Response.seeOther(UriBuilder.fromPath(Urls.SINGLE_IDP_HOMEPAGE_RESOURCE).build(idpName))
                .cookie(new HttpOnlyNewCookie(
                        CookieNames.SESSION_COOKIE_NAME,
                        "",
                        "/",
                        "",
                        0,
                        false
                        ))
                .build();
    }
}
