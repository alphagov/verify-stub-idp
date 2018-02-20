package uk.gov.ida.stub.idp.builders;

import com.google.common.base.Optional;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.dtos.IdpUserDto;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

public class IdpUserDtoBuilder {

    private Optional<String> pid = absent();
    private Optional<MatchingDatasetValue<String>> firstName = absent();
    private Optional<MatchingDatasetValue<String>> middleNames = absent();
    private List<MatchingDatasetValue<String>> surnames = new ArrayList<>();
    private Optional<MatchingDatasetValue<Gender>> gender = absent();
    private Optional<MatchingDatasetValue<LocalDate>> dateOfBirth = absent();
    private Optional<Address> address = absent();
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
        this.pid = fromNullable(pid);
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
        this.firstName = fromNullable(firstName);
        return this;
    }

    public IdpUserDtoBuilder withMiddleNames(final MatchingDatasetValue<String> middleNames) {
        this.middleNames = fromNullable(middleNames);
        return this;
    }

    public IdpUserDtoBuilder addSurname(final MatchingDatasetValue<String> surname) {
        this.surnames.add(surname);
        return this;
    }

    public IdpUserDtoBuilder withGender(final MatchingDatasetValue<Gender> gender) {
        this.gender = fromNullable(gender);
        return this;
    }

    public IdpUserDtoBuilder withDateOfBirth(final MatchingDatasetValue<LocalDate> dateOfBirth) {
        this.dateOfBirth = fromNullable(dateOfBirth);
        return this;
    }

    public IdpUserDtoBuilder withAddress(final Address address) {
        this.address = fromNullable(address);
        return this;
    }

    public IdpUserDtoBuilder withLevelOfAssurance(final String levelOfAssurance) {
        this.levelOfAssurance = levelOfAssurance;
        return this;
    }
}
