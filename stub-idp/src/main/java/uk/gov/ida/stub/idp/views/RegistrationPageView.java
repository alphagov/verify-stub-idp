package uk.gov.ida.stub.idp.views;

public class RegistrationPageView extends IdpPageView {

    public RegistrationPageView(String name, String idpId, String errorMessage, String assetId) {
        super("registrationPage.ftl", name, idpId, errorMessage, assetId);
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

}
