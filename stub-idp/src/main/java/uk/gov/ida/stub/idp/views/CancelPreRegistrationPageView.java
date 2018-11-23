package uk.gov.ida.stub.idp.views;

public class CancelPreRegistrationPageView extends IdpPageView {

    public CancelPreRegistrationPageView(String name, String idpId, String errorMessage, String assetId) {
        super("cancelPreRegistrationPage.ftl", name, idpId, errorMessage, assetId);
    }

    public String getPageTitle() {
        return String.format("Cancelled pre-registration for %s", getName());
    }

}
