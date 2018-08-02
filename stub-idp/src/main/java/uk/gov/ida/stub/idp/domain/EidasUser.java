package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Gender;

import java.util.Objects;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EidasUser {

    private String firstName;
    private final Optional<String> firstNameNonLatin;
    private String familyName;
    private final Optional<String> familyNameNonLatin;
    private String persistentId;
    private LocalDate dateOfBirth;
    private Optional<EidasAddress> address;
    private Optional<Gender> gender;

    @JsonCreator
    public EidasUser(@JsonProperty("firstName") String firstName,
                     @JsonProperty("firstNameNonLatin") Optional<String> firstNameNonLatin,
                     @JsonProperty("familyName") String familyName,
                     @JsonProperty("familyNameNonLatin") Optional<String> familyNameNonLatin,
                     @JsonProperty("persistentId") String persistentId,
                     @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
                     @JsonProperty("address") Optional<EidasAddress> address,
                     @JsonProperty("gender") Optional<Gender> gender) {
        this.firstName = firstName;
        this.firstNameNonLatin = firstNameNonLatin;
        this.familyName = familyName;
        this.familyNameNonLatin = familyNameNonLatin;
        this.persistentId = persistentId;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public Optional<String> getFirstNameNonLatin() {
        return firstNameNonLatin;
    }

    public String getFamilyName() {
        return familyName;
    }

    public Optional<String> getFamilyNameNonLatin() {
        return familyNameNonLatin;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public Optional<EidasAddress> getAddress() {
        return address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<Gender> getGender() {
        return gender;
    }

    public void setAddress(Optional<EidasAddress> address) {
        this.address = address;
    }

    public void setGender(Optional<Gender> gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EidasUser eidasUser = (EidasUser) o;
        return Objects.equals(firstName, eidasUser.firstName) &&
                Objects.equals(firstNameNonLatin, eidasUser.firstNameNonLatin) &&
                Objects.equals(familyName, eidasUser.familyName) &&
                Objects.equals(familyNameNonLatin, eidasUser.familyNameNonLatin) &&
                Objects.equals(persistentId, eidasUser.persistentId) &&
                Objects.equals(dateOfBirth, eidasUser.dateOfBirth) &&
                Objects.equals(address, eidasUser.address) &&
                Objects.equals(gender, eidasUser.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, firstNameNonLatin, familyName, familyNameNonLatin, persistentId, dateOfBirth, address, gender);
    }
}
