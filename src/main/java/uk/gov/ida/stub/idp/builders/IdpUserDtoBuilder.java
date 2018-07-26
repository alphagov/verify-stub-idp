package uk.gov.ida.stub.idp.builders;

import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.dtos.IdpUserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IdpUserDtoBuilder {

    private Optional<String> pid = Optional.empty();
    private Optional<MatchingDatasetValue<String>> firstName = Optional.empty();
    private Optional<MatchingDatasetValue<String>> middleNames = Optional.empty();
    private List<MatchingDatasetValue<String>> surnames = new ArrayList<>();
    private Optional<MatchingDatasetValue<Gender>> gender = Optional.empty();
    private Optional<MatchingDatasetValue<LocalDate>> dateOfBirth = Optional.empty();
    private Optional<Address> address = Optional.empty();
    private String userName;
    private String password;
    private String levelOfAssurance;

    public static IdpUserDtoBuilder anIdpUserDto() {
        return new IdpUserDtoBuilder();
    }

    public IdpUserDto build() {
        return new IdpUserDto(
                pid,
                userName,
                password,
                firstName,
                middleNames,
                surnames,
                gender,
                dateOfBirth,
                address,
                levelOfAssurance
        );
    }

    public IdpUserDtoBuilder withPid(String pid) {
        this.pid = Optional.ofNullable(pid);
        return this;
    }

    public IdpUserDtoBuilder withUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public IdpUserDtoBuilder withPassword(final String password) {
        this.password = password;
        return this;
    }

    public IdpUserDtoBuilder withFirsName(final MatchingDatasetValue<String> firstName) {
        this.firstName = Optional.ofNullable(firstName);
        return this;
    }

    public IdpUserDtoBuilder withMiddleNames(final MatchingDatasetValue<String> middleNames) {
        this.middleNames = Optional.ofNullable(middleNames);
        return this;
    }

    public IdpUserDtoBuilder addSurname(final MatchingDatasetValue<String> surname) {
        this.surnames.add(surname);
        return this;
    }

    public IdpUserDtoBuilder withGender(final MatchingDatasetValue<Gender> gender) {
        this.gender = Optional.ofNullable(gender);
        return this;
    }

    public IdpUserDtoBuilder withDateOfBirth(final MatchingDatasetValue<LocalDate> dateOfBirth) {
        this.dateOfBirth = Optional.ofNullable(dateOfBirth);
        return this;
    }

    public IdpUserDtoBuilder withAddress(final Address address) {
        this.address = Optional.ofNullable(address);
        return this;
    }

    public IdpUserDtoBuilder withLevelOfAssurance(final String levelOfAssurance) {
        this.levelOfAssurance = levelOfAssurance;
        return this;
    }
}
