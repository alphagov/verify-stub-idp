package uk.gov.ida.stub.idp.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.LocalDate;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EidasUserDto {

    private Optional<String> pid = Optional.empty();
    private String username;
    private String password;
    private MatchingDatasetValue<String> firstName;
    private Optional<MatchingDatasetValue<String>> firstNameNonLatin = Optional.empty();
    private MatchingDatasetValue<String> familyName;
    private Optional<MatchingDatasetValue<String>> familyNameNonLatin = Optional.empty();
    private MatchingDatasetValue<LocalDate> dateOfBirth;
    private String levelOfAssurance;

    @SuppressWarnings("unused")
    private EidasUserDto() {}

    @JsonCreator
    public EidasUserDto(
            @JsonProperty("pid") Optional<String> pid,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("firstName") MatchingDatasetValue<String> firstName,
            @JsonProperty("firstNameNonLatin") Optional<MatchingDatasetValue<String>> firstNameNonLatin,
            @JsonProperty("surname") MatchingDatasetValue<String> familyName,
            @JsonProperty("surnameNonLatin") Optional<MatchingDatasetValue<String>> familyNameNonLatin,
            @JsonProperty("dateOfBirth") MatchingDatasetValue<LocalDate> dateOfBirth,
            @JsonProperty("levelOfAssurance") String levelOfAssurance) {

        this.pid = pid;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.firstNameNonLatin = firstNameNonLatin;
        this.familyName = familyName;
        this.familyNameNonLatin = familyNameNonLatin;
        this.dateOfBirth = dateOfBirth;
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

    public MatchingDatasetValue<String> getFirstName() {
        return firstName;
    }

    public Optional<MatchingDatasetValue<String>> getFirstNameNonLatin() {
        return firstNameNonLatin;
    }

    public MatchingDatasetValue<String> getFamilyName() {
        return familyName;
    }

    public Optional<MatchingDatasetValue<String>> getFamilyNameNonLatin() {
        return familyNameNonLatin;
    }

    public MatchingDatasetValue<LocalDate> getDateOfBirth() {
        return dateOfBirth;
    }

    public String getLevelOfAssurance() {
        return levelOfAssurance;
    }

    public static EidasUserDto fromEidasUser(DatabaseEidasUser eidasUser) {
        return new EidasUserDto(
                Optional.ofNullable(eidasUser.getPersistentId()),
                eidasUser.getUsername(),
                eidasUser.getPassword(),
                eidasUser.getFirstname(),
                eidasUser.getNonLatinFirstname(),
                eidasUser.getSurname(),
                eidasUser.getNonLatinSurname(),
                eidasUser.getDateOfBirth(),
                eidasUser.getLevelOfAssurance().toString()
        );
    }
}
