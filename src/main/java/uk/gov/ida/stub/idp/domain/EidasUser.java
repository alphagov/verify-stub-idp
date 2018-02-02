package uk.gov.ida.stub.idp.domain;

import org.joda.time.LocalDate;

import java.util.Optional;

public class EidasUser {

    private String firstName;
    private String familyName;
    private String persistentId;
    private EidasAddress address;
    private LocalDate dateOfBirth;
    private Optional<Gender> gender;

    public EidasUser(String firstName, String familyName,
                     String persistentId, EidasAddress address, LocalDate dateOfBirth, Optional<Gender> gender) {
        this.firstName = firstName;
        this.familyName = familyName;
        this.persistentId = persistentId;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
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

    public EidasAddress getAddress() {
        return address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<Gender> getGender() {
        return gender;
    }
}
