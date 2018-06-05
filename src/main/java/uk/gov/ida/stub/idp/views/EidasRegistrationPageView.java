package uk.gov.ida.stub.idp.views;

public class EidasRegistrationPageView extends IdpPageView {

    public EidasRegistrationPageView(String name, String idpId, String errorMessage, String assetId) {
        super("eidasRegistrationPage.ftl", name, idpId, errorMessage, assetId);
    }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

}
