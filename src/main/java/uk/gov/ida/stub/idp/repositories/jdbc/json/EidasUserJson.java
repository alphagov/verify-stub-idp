package uk.gov.ida.stub.idp.repositories.jdbc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.io.Serializable;
import java.util.Objects;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;

@JsonSerialize(include = NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EidasUserJson implements Serializable {
    private String username;
    private String persistentId;
    private String password;
    private MatchingDatasetValue<String> firstname;
    private MatchingDatasetValue<String> surname;
    private MatchingDatasetValue<LocalDate> dateOfBirth;
    private AuthnContext levelOfAssurance;

    private EidasUserJson() {
    }

    public EidasUserJson(
        String username,
        String persistentId,
        String password,
        MatchingDatasetValue<String> firstname,
        MatchingDatasetValue<String> surname,
        MatchingDatasetValue<LocalDate> dateOfBirth,
        AuthnContext levelOfAssurance) {

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
        if (!(o instanceof EidasUserJson)) return false;
        EidasUserJson eidasUser = (EidasUserJson) o;
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
        return "DatabaseEidasUser{" +
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
