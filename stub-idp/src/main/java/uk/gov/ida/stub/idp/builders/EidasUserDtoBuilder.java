package uk.gov.ida.stub.idp.builders;

import org.joda.time.LocalDate;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.dtos.EidasUserDto;

import java.util.Optional;

public class EidasUserDtoBuilder {

    private Optional<String> pid = Optional.empty();
    private MatchingDatasetValue<String> firstName;
    private Optional<MatchingDatasetValue<String>> firstNameNonLatin = Optional.empty();
    private MatchingDatasetValue<String> familyName;
    private Optional<MatchingDatasetValue<String>> familyNameNonLatin = Optional.empty();
    private MatchingDatasetValue<LocalDate> dateOfBirth;
    private String userName;
    private String password;
    private String levelOfAssurance;

    public static EidasUserDtoBuilder anEidasUserDto() {
        return new EidasUserDtoBuilder();
    }

    public EidasUserDto build() {
        return new EidasUserDto(
                pid,
                userName,
                password,
                firstName,
                firstNameNonLatin,
                familyName,
                familyNameNonLatin,
                dateOfBirth,
                levelOfAssurance
        );
    }

    public EidasUserDtoBuilder withPid(String pid) {
        this.pid = Optional.ofNullable(pid);
        return this;
    }

    public EidasUserDtoBuilder withUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public EidasUserDtoBuilder withPassword(final String password) {
        this.password = password;
        return this;
    }

    public EidasUserDtoBuilder withFirsName(final MatchingDatasetValue<String> firstName) {
        this.firstName = firstName;
        return this;
    }

    public EidasUserDtoBuilder withFirstNameNonLatin(final MatchingDatasetValue<String> firstNameNonLatin) {
        this.firstNameNonLatin = Optional.ofNullable(firstNameNonLatin);
        return this;
    }

    public EidasUserDtoBuilder withFamilyName(final MatchingDatasetValue<String> familyName) {
        this.familyName = familyName;
        return this;
    }

    public EidasUserDtoBuilder withFamilyNameNonLatin(final MatchingDatasetValue<String> familyNameNonLatin) {
        this.familyNameNonLatin = Optional.of(familyNameNonLatin);
        return this;
    }

    public EidasUserDtoBuilder withDateOfBirth(final MatchingDatasetValue<LocalDate> dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public EidasUserDtoBuilder withLevelOfAssurance(final String levelOfAssurance) {
        this.levelOfAssurance = levelOfAssurance;
        return this;
    }
}
