package uk.gov.ida.stub.idp.domain;


import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IdpUser implements Serializable{
    private final String username;
    private final String persistentId;
    private final String password;
    private final List<SimpleMdsValue<String>> firstnames;
    private final List<SimpleMdsValue<String>> middleNames;
    private final List<SimpleMdsValue<String>> surnames;
    private final Optional<SimpleMdsValue<Gender>> gender;
    private final List<SimpleMdsValue<LocalDate>> dateOfBirths;
    private final List<Address> addresses;
    private final AuthnContext levelOfAssurance;

    public IdpUser(
            String username,
            String persistentId,
            String password,
            List<SimpleMdsValue<String>> firstnames,
            List<SimpleMdsValue<String>> middleNames,
            List<SimpleMdsValue<String>> surnames,
            Optional<SimpleMdsValue<Gender>> gender,
            List<SimpleMdsValue<LocalDate>> dateOfBirths,
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

    public List<SimpleMdsValue<String>> getFirstnames() {
        return firstnames;
    }

    public List<SimpleMdsValue<String>> getMiddleNames() {
        return middleNames;
    }

    public List<SimpleMdsValue<String>> getSurnames() {
        return surnames;
    }

    public Optional<SimpleMdsValue<Gender>> getGender() {
        return gender;
    }

    public List<SimpleMdsValue<LocalDate>> getDateOfBirths() {
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

    public static IdpUser fromDatabaseUser(DatabaseIdpUser databaseIdpUser) {
        return new IdpUser(
                databaseIdpUser.getUsername(),
                databaseIdpUser.getPersistentId(),
                databaseIdpUser.getPassword(),
                databaseIdpUser.getFirstnames().stream().map(MatchingDatasetValue::asSimpleMdsValue).collect(Collectors.toList()),
                databaseIdpUser.getMiddleNames().stream().map(MatchingDatasetValue::asSimpleMdsValue).collect(Collectors.toList()),
                databaseIdpUser.getSurnames().stream().map(MatchingDatasetValue::asSimpleMdsValue).collect(Collectors.toList()),
                databaseIdpUser.getGender().map(MatchingDatasetValue::asSimpleMdsValue),
                databaseIdpUser.getDateOfBirths().stream().map(MatchingDatasetValue::asSimpleMdsValue).collect(Collectors.toList()),
                databaseIdpUser.getAddresses(),
                databaseIdpUser.getLevelOfAssurance()
        );
    }
}
