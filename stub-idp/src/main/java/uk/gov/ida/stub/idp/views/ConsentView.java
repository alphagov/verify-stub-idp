package uk.gov.ida.stub.idp.views;

import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.views.helpers.IdpUserHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ConsentView extends IdpPageView {
    private final boolean userLOADidNotMatch;
    private final AuthnContext userLevelOfAssurance;
    private final List<uk.gov.ida.saml.core.domain.AuthnContext> levelsOfAssurance;
    private final IdpUserHelper idpUserHelper;

    public ConsentView(String name, String idpId, String assetId, DatabaseIdpUser idpUser, boolean userLOADidNotMatch, AuthnContext userLevelOfAssurance, List<AuthnContext> levelsOfAssurance) {
        super("consent.ftl", name, idpId, null, assetId);
        this.userLOADidNotMatch = userLOADidNotMatch;
        this.userLevelOfAssurance = userLevelOfAssurance;
        this.levelsOfAssurance = levelsOfAssurance;
        this.idpUserHelper = new IdpUserHelper(idpUser);
    }

    public String getPageTitle() {
        return String.format("Consent page for %s", getName());
    }

    public String getLoaMismatchMessage() {
        String requestedLOAs = levelsOfAssurance.stream().map(Enum::name).collect(Collectors.joining(", "));
        return String.format("User's LOA [%s] does not match with requested LOAs [%s]", userLevelOfAssurance.name(), requestedLOAs);
    }

    public DatabaseIdpUser getIdpUser() {
        return idpUserHelper.getIdpUser();
    }

    public String getFirstName() {
        return idpUserHelper.getFirstName();
    }

    public String getSurname() {
        return idpUserHelper.getSurname();
    }

    public String getDateOfBirth() {
        return idpUserHelper.getDateOfBirth();
    }

    public String getGender() {
        return idpUserHelper.getGender();
    }

    public Address getAddress() {
        return idpUserHelper.getAddress();
    }

    public String getLoa() {
        return idpUserHelper.getLoa();
    }

    public boolean isUserLOADidNotMatch() {
        return userLOADidNotMatch;
    }
}
