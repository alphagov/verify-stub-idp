package uk.gov.ida.stub.idp.views;

import java.util.Optional;

public class EidasRegistrationPageView extends IdpPageView {

    public EidasRegistrationPageView(String name, String idpId, String errorMessage, String assetId, String csrfToken) {
        super("eidasRegistrationPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

}
