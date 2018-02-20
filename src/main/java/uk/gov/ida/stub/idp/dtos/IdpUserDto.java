package uk.gov.ida.stub.idp.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Lists.newArrayList;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IdpUserDto {

    private Optional<String> pid = absent();
    private String username;
    private String password;
    private Optional<SimpleMdsValue<String>> firstName = absent();
    private Optional<SimpleMdsValue<String>> middleNames = absent();
    private List<SimpleMdsValue<String>> surname = newArrayList();
    private Optional<SimpleMdsValue<Gender>> gender = absent();
    private Optional<SimpleMdsValue<LocalDate>> dateOfBirth = absent();
    private Optional<Address> address = absent();
    private String levelOfAssurance;

    @SuppressWarnings("unused")
    private IdpUserDto() {}

    public IdpUserDto(
            Optional<String> pid,
            String username,
            String password,
            Optional<SimpleMdsValue<String>> firstName,
            Optional<SimpleMdsValue<String>> middleNames,
            List<SimpleMdsValue<String>> surnames,
            Optional<SimpleMdsValue<Gender>> gender,
            Optional<SimpleMdsValue<LocalDate>> dateOfBirth,
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
        return Optional.fromNullable(pid).get();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Optional<SimpleMdsValue<String>> getFirstName() {
        return firstName;
    }

    public List<SimpleMdsValue<String>> getSurnames() {
        return surname;
    }

    public Optional<SimpleMdsValue<LocalDate>> getDateOfBirth() {
        return dateOfBirth;
    }

    public Optional<Address> getAddress() {
        return address;
    }

    public String getLevelOfAssurance() {
        return levelOfAssurance;
    }

    public Optional<SimpleMdsValue<String>> getMiddleNames() {
        return middleNames;
    }

    public Optional<SimpleMdsValue<Gender>> getGender() {
        return gender;
    }

    public static IdpUserDto fromIdpUser(IdpUser idpUser) {
        return new IdpUserDto(
                fromNullable(idpUser.getPersistentId()),
                idpUser.getUsername(),
                idpUser.getPassword(),
                getFirstValue(idpUser.getFirstnames()),
                getFirstValue(idpUser.getMiddleNames()),
                idpUser.getSurnames(),
                idpUser.getGender(),
                getFirstValue(idpUser.getDateOfBirths()),
                fromNullable(idpUser.getCurrentAddress()),
                idpUser.getLevelOfAssurance().toString()
        );
    }

    private static <T> Optional<SimpleMdsValue<T>> getFirstValue(List<SimpleMdsValue<T>> values) {
        if (values.isEmpty()) {
            return absent();
        }

        return fromNullable(values.get(0));
    }
}
