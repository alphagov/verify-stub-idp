package uk.gov.ida.stub.idp.views.helpers;

import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import static java.util.stream.Collectors.joining;

public class IdpUserHelper {

    private DatabaseIdpUser idpUser;

    public IdpUserHelper(DatabaseIdpUser databaseIdpUser) { this.idpUser = databaseIdpUser; }

    public DatabaseIdpUser getIdpUser() { return idpUser; }

    public String getFirstName() {
        return idpUser.getFirstnames()
                .stream()
                .findFirst()
                .orElseGet(this::createEmptySimpleMdsStringValue)
                .getValue();
    }

    private MatchingDatasetValue<String> createEmptySimpleMdsStringValue() {
        return new MatchingDatasetValue<>("", null, null, true);
    }

    public String getSurname() {
        return idpUser.getSurnames()
                .stream()
                .findFirst()
                .orElseGet(this::createEmptySimpleMdsStringValue)
                .getValue();
    }

    public String getSurnames() {
        return this.idpUser
                .getSurnames()
                .stream()
                .map(MatchingDatasetValue::getValue)
                .collect(joining(","));
    }

    public String getDateOfBirth() {
        return idpUser.getDateOfBirths()
                .stream()
                .findFirst()
                .map(MatchingDatasetValue::getValue)
                .map(d -> d.toString("dd/MM/yyyy"))
                .orElse("");
    }

    public String getGender() {
        return idpUser.getGender()
                .map(gender -> gender.getValue().getValue())
                .orElse("");
    }

    public Address getAddress() {
        return this.idpUser.getCurrentAddress();
    }

    public String getLoa() { return this.idpUser.getLevelOfAssurance().toString(); }

}
