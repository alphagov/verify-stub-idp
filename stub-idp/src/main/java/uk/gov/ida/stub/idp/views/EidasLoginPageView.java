package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.Urls;

public class EidasLoginPageView extends IdpPageView {

    public EidasLoginPageView(String name, String schemeId, String errorMessage, String assetId) {
        super("eidasLoginPage.ftl", name, schemeId, errorMessage, assetId);
    }

    public String getSignAssertionsCheckboxGroup() { return Urls.SIGN_ASSERTIONS_PARAM_CHECKBOX_GROUP; }

    public String getSignAssertionsCheckboxValue() { return Urls.SIGN_ASSERTIONS_PARAM_VALUE; }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

}
