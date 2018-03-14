package uk.gov.ida.stub.idp.domain;


import com.google.common.base.Optional;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static uk.gov.ida.stub.idp.domain.MatchingDatasetValue.fromSimpleMdsValue;

public class DatabaseIdpUser implements Serializable {
    private final String username;
    private final String persistentId;
    private final String password;
    private final List<MatchingDatasetValue<String>> firstnames;
    private final List<MatchingDatasetValue<String>> middleNames;
    private final List<MatchingDatasetValue<String>> surnames;
    private final Optional<MatchingDatasetValue<Gender>> gender;
    private final List<MatchingDatasetValue<LocalDate>> dateOfBirths;
    private final List<Address> addresses;
    private final AuthnContext levelOfAssurance;

    public DatabaseIdpUser(
        String username,
        String persistentId,
        String password,
        List<MatchingDatasetValue<String>> firstnames,
        List<MatchingDatasetValue<String>> middleNames,
        List<MatchingDatasetValue<String>> surnames,
        Optional<MatchingDatasetValue<Gender>> gender,
        List<MatchingDatasetValue<LocalDate>> dateOfBirths,
        List<Address> addresses,
        AuthnContext levelOfAssurance) {

        this.username = username;
        this.persistentId = persistentId;
        this.password = password;
        this.firstnames = firstnames;
        this.middleNames = middleNames;
        this.surnames = surnames;
        this.gender = gender;
        this.dateOfBirths = dateOfBirths;
        this.addresses = addresses;
        this.levelOfAssurance = levelOfAssurance;
    }

    public String getUsername() {
        return username;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public String getPassword() {
        return password;
    }

    public List<MatchingDatasetValue<String>> getFirstnames() {
        return firstnames;
    }

    public List<MatchingDatasetValue<String>> getMiddleNames() {
        return middleNames;
    }

    public List<MatchingDatasetValue<String>> getSurnames() {
        return surnames;
    }

    public Optional<MatchingDatasetValue<Gender>> getGender() {
        return gender;
    }

    public List<MatchingDatasetValue<LocalDate>> getDateOfBirths() {
        return dateOfBirths;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    // Basing this on the implementation in IdpAssertionToAssertionTransformer
    public Address getCurrentAddress() {
        if (addresses.isEmpty()) {
            return null;
        }
        return addresses.get(0);
    }

    public AuthnContext getLevelOfAssurance() {
        return levelOfAssurance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatabaseIdpUser)) return false;
        DatabaseIdpUser idpUser = (DatabaseIdpUser) o;
        return Objects.equals(username, idpUser.username) &&
            Objects.equals(persistentId, idpUser.persistentId) &&
            Objects.equals(password, idpUser.password) &&
            Objects.equals(firstnames, idpUser.firstnames) &&
            Objects.equals(middleNames, idpUser.middleNames) &&
            Objects.equals(surnames, idpUser.surnames) &&
            Objects.equals(gender, idpUser.gender) &&
            Objects.equals(dateOfBirths, idpUser.dateOfBirths) &&
            Objects.equals(addresses, idpUser.addresses) &&
            levelOfAssurance == idpUser.levelOfAssurance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, persistentId, password, firstnames, middleNames, surnames, gender, dateOfBirths, addresses, levelOfAssurance);
    }

    @Override
    public String toString() {
        return "DatabaseIdpUser{" +
            "username='" + username + '\'' +
            ", persistentId='" + persistentId + '\'' +
            ", password='" + password + '\'' +
            ", firstnames=" + firstnames +
            ", middleNames=" + middleNames +
            ", surnames=" + surnames +
            ", gender=" + gender +
            ", dateOfBirths=" + dateOfBirths +
            ", addresses=" + addresses +
            ", levelOfAssurance=" + levelOfAssurance +
            '}';
    }
}
