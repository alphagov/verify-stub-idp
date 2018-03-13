package uk.gov.ida.stub.idp.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IdpUserDto {

    private Optional<String> pid = Optional.empty();
    private String username;
    private String password;
    private Optional<MatchingDatasetValue<String>> firstName = Optional.empty();
    private Optional<MatchingDatasetValue<String>> middleNames = Optional.empty();
    private List<MatchingDatasetValue<String>> surname = new ArrayList<>();
    private Optional<MatchingDatasetValue<Gender>> gender = Optional.empty();
    private Optional<MatchingDatasetValue<LocalDate>> dateOfBirth = Optional.empty();
    private Optional<Address> address = Optional.empty();
    private String levelOfAssurance;

    @SuppressWarnings("unused")
    private IdpUserDto() {}

    public IdpUserDto(
            Optional<String> pid,
            String username,
            String password,
            Optional<MatchingDatasetValue<String>> firstName,
            Optional<MatchingDatasetValue<String>> middleNames,
            List<MatchingDatasetValue<String>> surnames,
            Optional<MatchingDatasetValue<Gender>> gender,
            Optional<MatchingDatasetValue<LocalDate>> dateOfBirth,
            Optional<Address> address,
            String levelOfAssurance) {

        this.pid = pid;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.surname = surnames;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.levelOfAssurance = levelOfAssurance;
    }

    public Optional<String> getPid() {
        return Optional.ofNullable(pid).get();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Optional<MatchingDatasetValue<String>> getFirstName() {
        return firstName;
    }

    public List<MatchingDatasetValue<String>> getSurnames() {
        return surname;
    }

    public Optional<MatchingDatasetValue<LocalDate>> getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<Address> getAddress() {
        return address;
    }

    public String getLevelOfAssurance() {
        return levelOfAssurance;
    }

    public Optional<MatchingDatasetValue<String>> getMiddleNames() {
        return middleNames;
    }

    public Optional<MatchingDatasetValue<Gender>> getGender() {
        return gender;
    }

    public static IdpUserDto fromIdpUser(DatabaseIdpUser idpUser) {
        return new IdpUserDto(
                Optional.ofNullable(idpUser.getPersistentId()),
                idpUser.getUsername(),
                idpUser.getPassword(),
                idpUser.getFirstnames().stream().findFirst(),
                idpUser.getMiddleNames().stream().findFirst(),
                idpUser.getSurnames(),
                idpUser.getGender(),
                idpUser.getDateOfBirths().stream().findFirst(),
                Optional.ofNullable(idpUser.getCurrentAddress()),
                idpUser.getLevelOfAssurance().toString()
        );
    }
}
