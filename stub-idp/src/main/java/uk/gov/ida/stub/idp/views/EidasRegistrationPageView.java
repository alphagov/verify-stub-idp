package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.Urls;

public class EidasRegistrationPageView extends IdpPageView {

    public EidasRegistrationPageView(String name, String idpId, String errorMessage, String assetId) {
        super("eidasRegistrationPage.ftl", name, idpId, errorMessage, assetId);
    }

    public String getSignAssertionsCheckboxGroup() { return Urls.SIGN_ASSERTIONS_PARAM_CHECKBOX_GROUP; }

    public String getSignAssertionsCheckboxValue() { return Urls.SIGN_ASSERTIONS_PARAM_VALUE; }

    public String getPageTitle() {
        return String.format("Registration for %s", getName());
    }

}
