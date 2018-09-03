package uk.gov.ida.stub.idp.resources.idp;

import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.services.SuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

@Path(Urls.HEADLESS_ROOT)
public class HeadlessIdpResource {

    private static final String IDP_NAME = "headless-idp";

    private final Function<String, IdaAuthnRequestFromHub> samlRequestTransformer;
    private final IdpStubsRepository idpStubsRepository;
    private final SuccessAuthnResponseService successAuthnResponseService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;

    @Inject
    public HeadlessIdpResource(
            Function<String, IdaAuthnRequestFromHub> samlRequestTransformer,
            IdpStubsRepository idpStubsRepository,
            SuccessAuthnResponseService successAuthnResponseService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory) {

        this.samlRequestTransformer = samlRequestTransformer;
        this.idpStubsRepository = idpStubsRepository;
        this.successAuthnResponseService = successAuthnResponseService;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
    }

    @POST
    public Response handlePost(
            @FormParam(Urls.SAML_REQUEST_PARAM) @NotNull String samlRequest,
            @FormParam(Urls.RELAY_STATE_PARAM) String relayState,
            @FormParam(Urls.CYCLE3_PARAM) boolean isC3,
            @Context HttpServletRequest httpServletRequest) {

        final String username = isC3?"headless-c3":"headless";
        final IdaAuthnRequestFromHub idaRequestFromHub = samlRequestTransformer.apply(samlRequest);
        final Optional<DatabaseIdpUser> idpUser = idpStubsRepository.getIdpWithFriendlyId(IDP_NAME).getUser(username, "bar");

        final IdpSession session = new IdpSession(SessionId.createNewSessionId(), idaRequestFromHub, relayState, null, null, null, null, null);
        session.setIdpUser(idpUser);

        final SamlResponse successResponse = successAuthnResponseService.getSuccessResponse(false, httpServletRequest.getRemoteAddr(), IDP_NAME, session);
        return samlResponseRedirectViewFactory.sendSamlMessage(successResponse);
    }

}
