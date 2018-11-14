package uk.gov.ida.stub.idp.views;

public class RegistrationPageView extends IdpPageView {
    private String path;

    public RegistrationPageView(String name, String idpId, String errorMessage, String assetId, String path) {
        super("registrationPage.ftl", name, idpId, errorMessage, assetId);
        this.path = path;
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

    public String getPath() { return path == null ? "" : path; }
}
