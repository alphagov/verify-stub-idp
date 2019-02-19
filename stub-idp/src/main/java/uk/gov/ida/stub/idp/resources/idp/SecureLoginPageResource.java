package uk.gov.ida.stub.idp.resources.idp;

import uk.gov.ida.stub.idp.cookies.CookieFactory;
import uk.gov.ida.stub.idp.filters.SessionCookieValueMustExistAsASession;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.services.IdpUserService;
import uk.gov.ida.stub.idp.services.NonSuccessAuthnResponseService;
import uk.gov.ida.stub.idp.views.SamlResponseRedirectViewFactory;

import javax.inject.Inject;

@SessionCookieValueMustExistAsASession
public class SecureLoginPageResource extends LoginPageResource {
    @Inject
    public SecureLoginPageResource(IdpStubsRepository idpStubsRepository,
                                   NonSuccessAuthnResponseService nonSuccessAuthnResponseService,
                                   SamlResponseRedirectViewFactory samlResponseRedirectViewFactory,
                                   IdpUserService idpUserService,
                                   IdpSessionRepository sessionRepository,
                                   CookieFactory cookieFactory) {
        super(idpStubsRepository, nonSuccessAuthnResponseService, samlResponseRedirectViewFactory, idpUserService, sessionRepository, cookieFactory);
    }
}
