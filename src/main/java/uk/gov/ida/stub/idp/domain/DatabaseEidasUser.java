package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AuthnContext;

import java.io.Serializable;
import java.util.Objects;

public class DatabaseEidasUser implements Serializable {
    private final String username;
    private final String persistentId;
    private final String password;
    private final MatchingDatasetValue<String> firstname;
    private final MatchingDatasetValue<String> surname;
    private final MatchingDatasetValue<LocalDate> dateOfBirth;
    private final AuthnContext levelOfAssurance;

    @JsonCreator
    public DatabaseEidasUser(
        @JsonProperty("username") String username,
        @JsonProperty("persistentId") String persistentId,
        @JsonProperty("password") String password,
        @JsonProperty("firstname") MatchingDatasetValue<String> firstname,
        @JsonProperty("surname") MatchingDatasetValue<String> surname,
        @JsonProperty("dateOfBirth") MatchingDatasetValue<LocalDate> dateOfBirth,
        @JsonProperty("levelOfAssurance") AuthnContext levelOfAssurance) {

        this.username = username;
        this.persistentId = persistentId;
        this.password = password;
        this.firstname = firstname;
        this.surname = surname;
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

    public MatchingDatasetValue<String> getSurname() {
        return surname;
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
        if (!(o instanceof DatabaseEidasUser)) return false;
        DatabaseEidasUser eidasUser = (DatabaseEidasUser) o;
        return Objects.equals(username, eidasUser.username) &&
            Objects.equals(persistentId, eidasUser.persistentId) &&
            Objects.equals(password, eidasUser.password) &&
            Objects.equals(firstname, eidasUser.firstname) &&
            Objects.equals(surname, eidasUser.surname) &&
            Objects.equals(dateOfBirth, eidasUser.dateOfBirth) &&
            levelOfAssurance == eidasUser.levelOfAssurance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, persistentId, password, firstname, surname, dateOfBirth, levelOfAssurance);
    }

    @Override
    public String toString() {
        return "DatabaseIdpUser{" +
            "username='" + username + '\'' +
            ", persistentId='" + persistentId + '\'' +
            ", password='" + password + '\'' +
            ", firstnames=" + firstname +
            ", surnames=" + surname +
            ", dateOfBirths=" + dateOfBirth +
            ", levelOfAssurance=" + levelOfAssurance +
            '}';
    }
}
