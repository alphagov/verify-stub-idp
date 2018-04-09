package uk.gov.ida.stub.idp.views;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ConsentView extends IdpPageView {
    private IdpUser idpUser;
    private final boolean userLOADidNotMatch;
    private final AuthnContext userLevelOfAssurance;
    private final List<uk.gov.ida.saml.core.domain.AuthnContext> levelsOfAssurance;

    public ConsentView(String name, String idpId, String assetId, IdpUser idpUser, boolean userLOADidNotMatch, AuthnContext userLevelOfAssurance, List<AuthnContext> levelsOfAssurance) {
        super("consent.ftl", name, idpId, null, assetId);
        this.idpUser = idpUser;
        this.userLOADidNotMatch = userLOADidNotMatch;
        this.userLevelOfAssurance = userLevelOfAssurance;
        this.levelsOfAssurance = levelsOfAssurance;
    }

    public String getPageTitle() {
        return String.format("Consent page for %s", getName());
    }

    public String getLoaMismatchMessage() {
        String requestedLOAs = levelsOfAssurance.stream().map(Enum::name).collect(Collectors.joining(", "));
        return String.format("User's LOA [%s] does not match with requested LOAs [%s]", userLevelOfAssurance.name(), requestedLOAs);
    }

    public String getFirstName() {
        if (!idpUser.getFirstnames().isEmpty()) {
            return idpUser.getFirstnames().get(0).getValue();
        }
        return createEmptySimpleMdsStringValue().getValue();
    }

    private SimpleMdsValue<String> createEmptySimpleMdsStringValue() {
        return new SimpleMdsValue<>("", null, null, true);
    }

    public String getSurname() {
        Collection<String> surnameValues = Collections2.transform(this.idpUser.getSurnames(), new Function<SimpleMdsValue<String>, String>() {
            @Nullable
            @Override
            public String apply(@Nullable SimpleMdsValue<String> input) {
                return input != null ? input.getValue() : null;
            }
        });
        return StringUtils.join(surnameValues, ",");
    }

    public String getDateOfBirth() {
        if (!idpUser.getDateOfBirths().isEmpty()) {
            return idpUser.getDateOfBirths().get(0).getValue().toString("dd/MM/yyyy");
        }
        return "";
    }

    public String getGender() {
        if (idpUser.getGender().isPresent()) {
            return idpUser.getGender().get().getValue().getValue();
        }
        return "";
    }

    public Address getAddress() {
        return this.idpUser.getCurrentAddress();
    }

    public String getLoa() { return this.idpUser.getLevelOfAssurance().toString(); }

    public boolean isUserLOADidNotMatch() {
        return userLOADidNotMatch;
    }
}
