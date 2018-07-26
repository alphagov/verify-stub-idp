package uk.gov.ida.stub.idp.views;

public class LoginPageView extends IdpPageView {

    public LoginPageView(String name, String idpId, String errorMessage, String assetId) {
        super("loginPage.ftl", name, idpId, errorMessage, assetId);
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

}
