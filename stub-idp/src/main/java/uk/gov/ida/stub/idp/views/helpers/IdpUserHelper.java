package uk.gov.ida.stub.idp.views.helpers;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import javax.annotation.Nullable;
import java.util.Collection;

public class IdpUserHelper {

    DatabaseIdpUser idpUser;

    public IdpUserHelper(DatabaseIdpUser databaseIdpUser) { this.idpUser = databaseIdpUser; }

    public DatabaseIdpUser getIdpUser() { return idpUser; }

    public String getFirstName() {
        if (!idpUser.getFirstnames().isEmpty()) {
            return idpUser.getFirstnames().get(0).getValue();
        }
        return createEmptySimpleMdsStringValue().getValue();
    }

    private MatchingDatasetValue<String> createEmptySimpleMdsStringValue() {
        return new MatchingDatasetValue<>("", null, null, true);
    }

    public String getSurname() {
        if (!idpUser.getSurnames().isEmpty()) {
            return idpUser.getSurnames().get(0).getValue();
        }
        return createEmptySimpleMdsStringValue().getValue();
    }

    public String getSurnames() {
        Collection<String> surnameValues = Collections2.transform(this.idpUser.getSurnames(), new Function<MatchingDatasetValue<String>, String>() {
            @Nullable
            @Override
            public String apply(@Nullable MatchingDatasetValue<String> input) {
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

}
