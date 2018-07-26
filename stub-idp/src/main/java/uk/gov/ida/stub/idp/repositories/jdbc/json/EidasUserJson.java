package uk.gov.ida.stub.idp.repositories.jdbc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EidasUserJson implements Serializable {
    private String username;
    private String persistentId;
    private String password;
    private MatchingDatasetValue<String> firstname;
    private Optional<MatchingDatasetValue<String>> nonLatinFirstname;
    private MatchingDatasetValue<String> surname;
    private Optional<MatchingDatasetValue<String>> nonLatinSurname;
    private MatchingDatasetValue<LocalDate> dateOfBirth;
    private AuthnContext levelOfAssurance;

    private EidasUserJson() {
    }

    public EidasUserJson(
        String username,
        String persistentId,
        String password,
        MatchingDatasetValue<String> firstname,
        Optional<MatchingDatasetValue<String>> nonLatinFirstname,
        MatchingDatasetValue<String> surname,
        Optional<MatchingDatasetValue<String>> nonLatinSurname,
        MatchingDatasetValue<LocalDate> dateOfBirth,
        AuthnContext levelOfAssurance) {

        this.username = username;
        this.persistentId = persistentId;
        this.password = password;
        this.firstname = firstname;
        this.nonLatinFirstname = nonLatinFirstname;
        this.surname = surname;
        this.nonLatinSurname = nonLatinSurname;
        this.dateOfBirth = dateOfBirth;
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

    public MatchingDatasetValue<String> getFirstname() {
        return firstname;
    }

    public Optional<MatchingDatasetValue<String>> getNonLatinFirstname() {
        return nonLatinFirstname;
    }

    public MatchingDatasetValue<String> getSurname() {
        return surname;
    }

    public Optional<MatchingDatasetValue<String>> getNonLatinSurname() {
        return nonLatinSurname;
    }

    public MatchingDatasetValue<LocalDate> getDateOfBirth() {
        return dateOfBirth;
    }

    public AuthnContext getLevelOfAssurance() {
        return levelOfAssurance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EidasUserJson that = (EidasUserJson) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(persistentId, that.persistentId) &&
                Objects.equals(password, that.password) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(nonLatinFirstname, that.nonLatinFirstname) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(nonLatinSurname, that.nonLatinSurname) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                levelOfAssurance == that.levelOfAssurance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, persistentId, password, firstname, nonLatinFirstname, surname, nonLatinSurname, dateOfBirth, levelOfAssurance);
    }

    @Override
    public String toString() {
        return "EidasUserJson{" +
                "username='" + username + '\'' +
                ", persistentId='" + persistentId + '\'' +
                ", password='" + password + '\'' +
                ", firstname=" + firstname +
                ", nonLatinFirstname=" + nonLatinFirstname +
                ", surname=" + surname +
                ", nonLatinSurname=" + nonLatinSurname +
                ", dateOfBirth=" + dateOfBirth +
                ", levelOfAssurance=" + levelOfAssurance +
                '}';
    }
}
