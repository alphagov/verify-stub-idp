package uk.gov.ida.stub.idp.resources.idp;

import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;

@SessionCookieValueMustExistAsASession
public class SecureRegistrationPageResource extends RegistrationPageResource {

    @Inject
    public SecureRegistrationPageResource(IdpStubsRepository idpStubsRepository, IdpUserService idpUserService, SamlResponseRedirectViewFactory samlResponseRedirectViewFactory, NonSuccessAuthnResponseService nonSuccessAuthnResponseService, IdpSessionRepository idpSessionRepository) {
        super(idpStubsRepository, idpUserService, samlResponseRedirectViewFactory, nonSuccessAuthnResponseService, idpSessionRepository);
    }
}
