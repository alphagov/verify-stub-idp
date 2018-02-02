package uk.gov.ida.stub.idp.resources;

import com.google.common.base.Strings;
import org.joda.time.LocalDate;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.EidasAddress;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.Gender;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.views.EidasLoginPageView;
import uk.gov.ida.stub.idp.views.ErrorMessageType;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.EIDAS_LOGIN_RESOURCE)
@Produces(MediaType.TEXT_HTML)
@SessionCookieValueMustExistAsASession
public class EidasLoginPageResource {

    private final SessionRepository sessionRepository;

    @Inject
    public EidasLoginPageResource(
            SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeId,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        checkSession(schemeId, sessionCookie);

        return Response.ok()
                .entity(new EidasLoginPageView("European ID Scheme", schemeId, errorMessage.orElse(NO_ERROR).getMessage(), schemeId))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @PathParam(Urls.SCHEME_ID_PARAM) @NotNull String schemeName,
            @FormParam(Urls.USERNAME_PARAM) String username,
            @FormParam(Urls.PASSWORD_PARAM) String password,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        Session session = checkSession(schemeName, sessionCookie);

        EidasAddress address = new EidasAddress("","","","","","",
                "10 Europe Street","","75001");
        EidasUser eidasUser = new EidasUser("Bob", "Smith", "pid", address,
                new LocalDate(1988, 10, 10), Optional.of(Gender.MALE));
        session.setEidasUser(eidasUser);

        return Response.seeOther(UriBuilder.fromPath(Urls.EIDAS_CONSENT_RESOURCE)
                .build(schemeName))
                .build();
    }

    private Session checkSession(String idpName, SessionId sessionCookie) {
        if (sessionCookie == null || Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Unable to locate session cookie for " + idpName))).build());
        }

        Optional<Session> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(format(("Session is invalid for " + idpName))).build());
        }

        return session.get();
    }

}
