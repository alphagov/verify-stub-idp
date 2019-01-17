package uk.gov.ida.stub.idp.views;

import java.util.Optional;

public class LoginPageView extends IdpPageView {

    public LoginPageView(String name, String idpId, String errorMessage, String assetId, String csrfToken) {
        super("loginPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

}
