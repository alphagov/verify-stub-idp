package uk.gov.ida.stub.idp.repositories.jdbc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdpUserJson {

    private String username;
    private String persistentId;
    private String password;
    private List<MatchingDatasetValue<String>> firstnames;
    private List<MatchingDatasetValue<String>> middleNames;
    private List<MatchingDatasetValue<String>> surnames;
    private Optional<MatchingDatasetValue<Gender>> gender;
    private List<MatchingDatasetValue<LocalDate>> dateOfBirths;
    private List<Address> addresses;
    private AuthnContext levelOfAssurance;

    private IdpUserJson() {
    }

    public IdpUserJson(
        String username,
        String persistentId,
        String password,
        List<MatchingDatasetValue<String>> firstnames,
        List<MatchingDatasetValue<String>> middleNames,
        List<MatchingDatasetValue<String>> surnames,
        Optional<MatchingDatasetValue<Gender>> gender,
        List<MatchingDatasetValue<LocalDate>> dateOfBirths,
        List<Address> addresses,
        AuthnContext levelOfAssurance
    ) {
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

    public AuthnContext getLevelOfAssurance() {
        return levelOfAssurance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdpUserJson)) return false;
        IdpUserJson that = (IdpUserJson) o;
        return Objects.equals(username, that.username) &&
            Objects.equals(persistentId, that.persistentId) &&
            Objects.equals(password, that.password) &&
            Objects.equals(firstnames, that.firstnames) &&
            Objects.equals(middleNames, that.middleNames) &&
            Objects.equals(surnames, that.surnames) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(dateOfBirths, that.dateOfBirths) &&
            Objects.equals(addresses, that.addresses) &&
            levelOfAssurance == that.levelOfAssurance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, persistentId, password, firstnames, middleNames, surnames, gender, dateOfBirths, addresses, levelOfAssurance);
    }

    @Override
    public String toString() {
        return "IdpUserJson{" +
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
