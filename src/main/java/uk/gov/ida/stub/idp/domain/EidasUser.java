package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Gender;

import java.util.Optional;

public class EidasUser {

    private String firstName;
    private String familyName;
    private String persistentId;
    private LocalDate dateOfBirth;
    private EidasAddress address;
    private Gender gender;

    @JsonCreator
    public EidasUser(@JsonProperty("firstName") String firstName, @JsonProperty("familyName") String familyName, @JsonProperty("persistentId") String persistentId,
                     @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("address") EidasAddress address, @JsonProperty("gender") Gender gender) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.persistentId = persistentId;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public Optional<EidasAddress> getAddress() {
        return Optional.ofNullable(address);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    public void setAddress(EidasAddress address) {
        this.address = address;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EidasUser)) return false;

        EidasUser eidasUser = (EidasUser) o;

        if (firstName != null ? !firstName.equals(eidasUser.firstName) : eidasUser.firstName != null) return false;
        if (familyName != null ? !familyName.equals(eidasUser.familyName) : eidasUser.familyName != null) return false;
        if (persistentId != null ? !persistentId.equals(eidasUser.persistentId) : eidasUser.persistentId != null)
            return false;
        if (dateOfBirth != null ? !dateOfBirth.equals(eidasUser.dateOfBirth) : eidasUser.dateOfBirth != null)
            return false;
        if (address != null ? !address.equals(eidasUser.address) : eidasUser.address != null) return false;
        return gender != null ? gender.equals(eidasUser.gender) : eidasUser.gender == null;
    }
}
