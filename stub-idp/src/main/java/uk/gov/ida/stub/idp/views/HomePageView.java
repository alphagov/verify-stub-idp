package uk.gov.ida.stub.idp.views;

import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.views.helpers.IdpUserHelper;

import java.util.Arrays;
import java.util.Optional;

public class HomePageView extends IdpPageView {
    Optional<DatabaseIdpUser> loggedInUser;
    IdpUserHelper idpUserHelper;

    public HomePageView(String name, String idpId, String errorMessage, String assetId, Optional<DatabaseIdpUser> loggedInUser) {
        super("homePage.ftl", name, idpId, errorMessage, assetId);
        this.loggedInUser = loggedInUser;
        idpUserHelper = new IdpUserHelper(loggedInUser.orElse(null));
    }

    public String getPageTitle() {
        return String.format("Welcome to %s", getName());
    }

    public boolean isUserLoggedIn() { return loggedInUser.isPresent(); }

    public String getUserFullName() {
        return String.format("%s %s", idpUserHelper.getFirstName(), idpUserHelper.getSurname());
    }

    public String getAnOrA() {
        String[] vowels = {"a","e","i","o","u"};
        return Arrays.stream(vowels).anyMatch(vowel -> super.getName().toLowerCase().startsWith(vowel)) ? "an" : "a";
    }
}
