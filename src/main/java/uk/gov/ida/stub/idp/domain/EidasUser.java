package uk.gov.ida.stub.idp.domain;

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

    public EidasUser(String firstName, String familyName, String persistentId,
                     LocalDate dateOfBirth, EidasAddress address, Gender gender) {
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
}
