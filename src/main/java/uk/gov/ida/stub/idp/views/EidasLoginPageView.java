package uk.gov.ida.stub.idp.views;

public class EidasLoginPageView extends IdpPageView {

    public EidasLoginPageView(String name, String schemeId, String errorMessage, String assetId) {
        super("eidasLoginPage.ftl", name, schemeId, errorMessage, assetId);
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

}
