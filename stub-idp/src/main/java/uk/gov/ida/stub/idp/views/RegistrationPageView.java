package uk.gov.ida.stub.idp.views;

import java.util.Optional;

public class RegistrationPageView extends IdpPageView {
    private String path;

    public RegistrationPageView(String name, String idpId, String errorMessage, String assetId, String path, String csrfToken) {
        super("registrationPage.ftl", name, idpId, errorMessage, assetId, Optional.ofNullable(csrfToken));
        this.path = path;
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

    public String getPath() { return path == null ? "" : path; }
}
