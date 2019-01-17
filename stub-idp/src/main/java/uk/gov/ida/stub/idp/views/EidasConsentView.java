package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.domain.EidasUser;

import java.util.Optional;

public class EidasConsentView extends IdpPageView {
    private EidasUser user;

    public EidasConsentView(String name, String idpId, String assetId, EidasUser user, String csrfToken) {
        super("eidasConsent.ftl", name, idpId, null, assetId, Optional.ofNullable(csrfToken));
        this.user = user;
    }

    public String getPageTitle() {
        return String.format("Consent page for %s", getName());
    }

    public EidasUser getUser() {
        return user;
    }
}
