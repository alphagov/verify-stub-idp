package uk.gov.ida.stub.idp.resources.singleidp;

import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.configuration.SingleIdpConfiguration;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.Service;
import uk.gov.ida.stub.idp.exceptions.FeatureNotEnabledException;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.services.ServiceListService;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.SingleIdpPromptPageView;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;

@Path(Urls.SINGLE_IDP_START_PROMPT_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class SingleIdpPromptPageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final ServiceListService serviceListService;
    private final SingleIdpConfiguration singleIdpConfiguration;
    private final IdpSessionRepository idpSessionRepository;

    @Inject
    public SingleIdpPromptPageResource(
            IdpStubsRepository idpStubsRepository,
            ServiceListService serviceListService,
            SingleIdpConfiguration singleIdpConfiguration,
            IdpSessionRepository idpSessionRepository) {
        this.idpStubsRepository = idpStubsRepository;
        this.serviceListService = serviceListService;
        this.singleIdpConfiguration = singleIdpConfiguration;
        this.idpSessionRepository = idpSessionRepository;
    }

    @GET
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) Optional<ErrorMessageType> errorMessage,
            @QueryParam(Urls.SOURCE_PARAM) Optional<String> source,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) throws FeatureNotEnabledException {
        if (!singleIdpConfiguration.isEnabled()) throw new FeatureNotEnabledException();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        UUID uuid = UUID.randomUUID();
        Optional<DatabaseIdpUser> idpUser = Optional.empty();

        List<Service> serviceList = serviceListService.getServices();
        if (source.isPresent() && source.get().equals(Urls.SOURCE_PARAM_PRE_REG_VALUE) && sessionCookie != null) {
            Optional<IdpSession> idpSession = idpSessionRepository.get(sessionCookie);
            if(idpSession.isPresent()) {
                idpUser = idpSession.get().getIdpUser();
            }
        }

        return Response.ok()
            .entity(
                new SingleIdpPromptPageView(idp.getDisplayName(),
                    idp.getIssuerId(),
                    errorMessage.orElse(NO_ERROR).getMessage(),
                    idp.getAssetId(),
                    serviceList,
                    singleIdpConfiguration.getVerifySubmissionUri(),
                    uuid,
                    idpUser.orElse(null)
                )
            )
            .build();
    }
}
