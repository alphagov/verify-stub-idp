package uk.gov.ida.stub.idp.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;
import uk.gov.ida.stub.idp.services.AuthnRequestReceiverService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Path(Urls.IDP_SAML2_SSO_RESOURCE)
public class IdpAuthenticationRequestReceiverResource {

    private static final Logger LOG = LoggerFactory.getLogger(IdpAuthenticationRequestReceiverResource.class);

    private final AuthnRequestReceiverService authnRequestReceiverService;
    private final CookieFactory cookieFactory;
    private final Boolean isSecureCookieEnabled;

    @Inject
    public IdpAuthenticationRequestReceiverResource(AuthnRequestReceiverService authnRequestReceiverService,
                                                    CookieFactory cookieFactory,
                                                    @Named("isSecureCookieEnabled") Boolean isSecureCookieEnabled) {
        this.authnRequestReceiverService = authnRequestReceiverService;
        this.cookieFactory = cookieFactory;
        this.isSecureCookieEnabled = isSecureCookieEnabled;
    }

    @POST
    public Response handlePost(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.SAML_REQUEST_PARAM) @NotNull String samlRequest,
            @FormParam(Urls.HINTS_PARAM) Set<String> idpHints,
            @FormParam(Urls.REGISTRATION_PARAM) Optional<Boolean> registration,
            @FormParam(Urls.RELAY_STATE_PARAM) String relayState,
            @FormParam(Urls.LANGUAGE_HINT_PARAM) Optional<IdpLanguageHint> languageHint) {
        LOG.debug("Received request for idp {} from HUB", idpName);

        final AuthnRequestReceiverService.SessionCreated sessionCreated = authnRequestReceiverService.handleAuthnRequest(idpName, samlRequest, idpHints, registration, relayState, languageHint);

        List<NewCookie> cookies = new ArrayList<>();
        if(isSecureCookieEnabled) {
            cookies.add(cookieFactory.createSecureCookieWithSecurelyHashedValue(sessionCreated.getIdpSessionId()));
        }
        cookies.add(cookieFactory.createSessionIdCookie(sessionCreated.getIdpSessionId()));

        return Response.seeOther(sessionCreated.getNextLocation())
                .cookie(cookies.toArray(new NewCookie[cookies.size()]))
                .build();
    }

}
