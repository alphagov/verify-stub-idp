package uk.gov.ida.stub.idp.resources.idp;

import com.google.common.base.Strings;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.Urls;
import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.cookies.CookieNames;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.domain.SubmitButtonValue;
import uk.gov.ida.stub.idp.exceptions.GenericStubIdpException;
import uk.gov.ida.stub.idp.exceptions.IncompleteRegistrationException;
import uk.gov.ida.stub.idp.exceptions.InvalidDateException;
import uk.gov.ida.stub.idp.exceptions.InvalidSessionIdException;
import uk.gov.ida.stub.idp.exceptions.InvalidUsernameOrPasswordException;
import uk.gov.ida.stub.idp.exceptions.UsernameAlreadyTakenException;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.SessionRepository;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.ErrorMessageType;
import uk.gov.ida.stub.idp.views.RegistrationPageView;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static java.text.MessageFormat.format;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INCOMPLETE_REGISTRATION;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_DATE;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_SESSION_ID;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.INVALID_USERNAME_OR_PASSWORD;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.NO_ERROR;
import static uk.gov.ida.stub.idp.views.ErrorMessageType.USERNAME_ALREADY_TAKEN;

@Path(Urls.REGISTER_RESOURCE)
@Produces(MediaType.TEXT_HTML)
public class RegistrationPageResource {

    private final IdpStubsRepository idpStubsRepository;
    private final IdpUserService idpUserService;
    private final SamlResponseRedirectViewFactory samlResponseRedirectViewFactory;
    private final NonSuccessAuthnResponseService nonSuccessAuthnResponseService;
    private final SessionRepository<IdpSession> sessionRepository;
    private final CookieFactory cookieFactory;
    private final IdpSessionRepository idpSessionRepository;

    @Inject
    public RegistrationPageResource(
            IdpStubsRepository idpStubsRepository,
            IdpUserService idpUserService,
            SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
            NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
            SessionRepository<IdpSession> sessionRepository,
            CookieFactory cookieFactory,
            IdpSessionRepository idpSessionRepository) {
        this.idpUserService = idpUserService;
        this.idpStubsRepository = idpStubsRepository;
        this.samlResponseRedirectViewFactory = samlResponseRedirectViewFactory;
        this.nonSuccessAuthnResponseService = nonSuccessAuthnResponseService;
        this.sessionRepository = sessionRepository;
        this.cookieFactory = cookieFactory;
        this.idpSessionRepository = idpSessionRepository;
    }

    @GET
    @SessionCookieValueMustExistAsASession
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) java.util.Optional<ErrorMessageType> errorMessage,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) @NotNull SessionId sessionCookie) {

        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + idpName), Response.Status.BAD_REQUEST);
        }

        if (!sessionRepository.containsSession(sessionCookie)) {
            throw new GenericStubIdpException(format("Session is invalid for " + idpName), Response.Status.BAD_REQUEST);
        }

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        return Response.ok(new RegistrationPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId(), null)).build();
    }

    @GET
    @Path(Urls.PRE_REGISTER_PATH)
    public Response get(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @QueryParam(Urls.ERROR_MESSAGE_PARAM) Optional<ErrorMessageType> errorMessage) {

        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);
        IdpSession idpSession = new IdpSession(
                new SessionId(UUID.randomUUID().toString()));
        final SessionId sessionId = idpSessionRepository.createSession(idpSession);
        return Response.ok(new RegistrationPageView(idp.getDisplayName(), idp.getFriendlyId(), errorMessage.orElse(NO_ERROR).getMessage(), idp.getAssetId(), Urls.PRE_REGISTER_PATH))
                .cookie(cookieFactory.createSessionIdCookie(sessionId))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response post(
            @PathParam(Urls.IDP_ID_PARAM) @NotNull String idpName,
            @FormParam(Urls.FIRSTNAME_PARAM) String firstname,
            @FormParam(Urls.SURNAME_PARAM) String surname,
            @FormParam(Urls.ADDRESS_LINE1_PARAM) String addressLine1,
            @FormParam(Urls.ADDRESS_LINE2_PARAM) String addressLine2,
            @FormParam(Urls.ADDRESS_TOWN_PARAM) String addressTown,
            @FormParam(Urls.ADDRESS_POST_CODE_PARAM) String addressPostCode,
            @FormParam(Urls.DATE_OF_BIRTH_PARAM) String dateOfBirth,
            @FormParam(Urls.USERNAME_PARAM) String username,
            @FormParam(Urls.PASSWORD_PARAM) String password,
            @FormParam(Urls.LEVEL_OF_ASSURANCE_PARAM) AuthnContext levelOfAssurance,
            @FormParam(Urls.SUBMIT_PARAM) @NotNull SubmitButtonValue submitButtonValue,
            @CookieParam(CookieNames.SESSION_COOKIE_NAME) SessionId sessionCookie) {

        if(sessionCookie == null) {
            return createErrorResponse(INVALID_SESSION_ID, idpName);
        }

        if (Strings.isNullOrEmpty(sessionCookie.toString())) {
            throw new GenericStubIdpException(format("Unable to locate session cookie for " + idpName), Response.Status.BAD_REQUEST);
        }

        Optional<IdpSession> session = sessionRepository.get(sessionCookie);

        if (!session.isPresent()) {
            throw new GenericStubIdpException(format("Session is invalid for " + idpName), Response.Status.BAD_REQUEST);
        }


        if (session.get().getIdaAuthnRequestFromHub() == null) {
            return preRegisterResponse(idpName, firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode, dateOfBirth, username, password, levelOfAssurance, submitButtonValue, sessionCookie);
        } else {
            return registerResponse(idpName, firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode, dateOfBirth, username, password, levelOfAssurance, submitButtonValue, sessionCookie, session);
        }
    }

    private Response preRegisterResponse(String idpName,
                                         String firstname,
                                         String surname,
                                         String addressLine1,
                                         String addressLine2,
                                         String addressTown,
                                         String addressPostCode,
                                         String dateOfBirth,
                                         String username,
                                         String password,
                                         AuthnContext levelOfAssurance,
                                         SubmitButtonValue submitButtonValue,
                                         SessionId sessionCookie) {
        switch (submitButtonValue) {
            case Cancel: {
                sessionRepository.deleteSession(sessionCookie);
                return Response.seeOther(UriBuilder.fromPath(Urls.CANCEL_PRE_REGISTER_RESOURCE).build(idpName)).build();
            }
            case Register: {
                try {
                    idpUserService.createAndAttachIdpUserToSession(idpName,
                            firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode,
                            levelOfAssurance, dateOfBirth, username, password, sessionCookie);
                } catch (InvalidSessionIdException e) {
                    return createErrorResponse(INVALID_SESSION_ID, idpName);
                } catch (IncompleteRegistrationException e) {
                    return createErrorResponse(INCOMPLETE_REGISTRATION, idpName);
                } catch (InvalidDateException e) {
                    return createErrorResponse(INVALID_DATE, idpName);
                } catch (UsernameAlreadyTakenException e) {
                    return createErrorResponse(USERNAME_ALREADY_TAKEN, idpName);
                } catch (InvalidUsernameOrPasswordException e) {
                    return createErrorResponse(INVALID_USERNAME_OR_PASSWORD, idpName);
                }

                return Response.seeOther(UriBuilder.fromPath(Urls.SINGLE_IDP_PROMPT_RESOURCE)
                        .queryParam(Urls.SOURCE_PARAM,Urls.SOURCE_PARAM_PRE_REG_VALUE)
                        .build(idpName))
                        .build();
            }
            default: {
                throw new GenericStubIdpException("unknown submit button value", Response.Status.BAD_REQUEST);
            }
        }

    }

    private Response registerResponse(String idpName,
                                  String firstname,
                                  String surname,
                                  String addressLine1,
                                  String addressLine2,
                                  String addressTown,
                                  String addressPostCode,
                                  String dateOfBirth,
                                  String username,
                                  String password,
                                  AuthnContext levelOfAssurance,
                                  SubmitButtonValue submitButtonValue,
                                  SessionId sessionCookie,
                                  Optional<IdpSession> session) {
        final String samlRequestId = session.get().getIdaAuthnRequestFromHub().getId();

        switch (submitButtonValue) {
            case Cancel: {

                session = sessionRepository.deleteAndGet(sessionCookie);

                final SamlResponse cancelResponse = nonSuccessAuthnResponseService.generateAuthnCancel(idpName, samlRequestId, session.get().getRelayState());
                return samlResponseRedirectViewFactory.sendSamlMessage(cancelResponse);
            }
            case Register: {
                try {
                    idpUserService.createAndAttachIdpUserToSession(idpName, firstname, surname, addressLine1, addressLine2, addressTown, addressPostCode, levelOfAssurance, dateOfBirth, username, password, sessionCookie);
                    return Response.seeOther(UriBuilder.fromPath(Urls.CONSENT_RESOURCE)
                            .build(idpName))
                            .build();
                } catch (InvalidSessionIdException e) {
                    return createErrorResponse(INVALID_SESSION_ID, idpName);
                } catch (IncompleteRegistrationException e) {
                    return createErrorResponse(INCOMPLETE_REGISTRATION, idpName);
                } catch (InvalidDateException e) {
                    return createErrorResponse(INVALID_DATE, idpName);
                } catch (UsernameAlreadyTakenException e) {
                    return createErrorResponse(USERNAME_ALREADY_TAKEN, idpName);
                } catch (InvalidUsernameOrPasswordException e) {
                    return createErrorResponse(INVALID_USERNAME_OR_PASSWORD, idpName);
                }
            }
            default: {
                throw new GenericStubIdpException("unknown submit button value", Response.Status.BAD_REQUEST);
            }
        }
    }

    private Response createErrorResponse(ErrorMessageType errorMessage, String idpName) {
        URI uri = UriBuilder.fromPath(Urls.REGISTER_RESOURCE)
                .queryParam(Urls.ERROR_MESSAGE_PARAM, errorMessage)
                .build(idpName);
        return Response.seeOther(uri).build();
    }
}
