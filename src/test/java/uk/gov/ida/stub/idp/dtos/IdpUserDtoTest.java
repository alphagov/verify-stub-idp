package uk.gov.ida.stub.idp.dtos;

import io.dropwizard.jackson.Jackson;
import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.builders.AddressBuilder;
import uk.gov.ida.stub.idp.builders.SimpleMdsValueBuilder;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTime.parse;
import static uk.gov.ida.stub.idp.builders.IdpUserDtoBuilder.anIdpUserDto;

public class IdpUserDtoTest {

    @Test
    public void shouldDeSerialiseJsonToObjectWhenAllFieldsArePopulated() throws IOException {

        IdpUserDto idpUserDtoFromJson = Jackson.newObjectMapper().readValue("{\"pid\":\"00754148-902f-4d94-b0db-cb1f7eb3fd84\",\"username\":\"user1\",\"password\":\"password\",\"firstName\":{\"value\":\"Fred\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"middleNames\":{\"value\":\"Flintstone\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"gender\":{\"value\":\"MALE\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"dateOfBirth\":{\"value\":[1970,1,1],\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"address\":{\"verified\":false,\"from\":978307200000,\"to\":1355270400000,\"postCode\":\"WC2B 6NH\",\"lines\":[\"Aviation House\",\"London\"],\"internationalPostCode\":null,\"uprn\":null},\"levelOfAssurance\":\"LEVEL_2\",\"surnames\":[{\"value\":\"Smith\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},{\"value\":\"Henry\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true}]}", IdpUserDto.class);

        IdpUserDto idpuserDto = anIdpUserDto()
                .withPid("00754148-902f-4d94-b0db-cb1f7eb3fd84")
                .withUserName("user1")
                .withPassword("password")
                .withFirsName(createSimpleMdsValue("Fred"))
                .withMiddleNames(createSimpleMdsValue("Flintstone"))
                .addSurname(createSimpleMdsValue("Smith"))
                .addSurname(createSimpleMdsValue("Henry"))
                .withGender(
                        SimpleMdsValueBuilder.<Gender>aSimpleMdsValue()
                                .withValue(Gender.MALE)
                                .withFrom(parse("1980-01-01"))
                                .withTo(parse("2013-01-01"))
                                .withVerifiedStatus(true)
                                .build()
                )
                .withDateOfBirth(
                        SimpleMdsValueBuilder.<LocalDate>aSimpleMdsValue()
                                .withValue(LocalDate.parse("1970-01-01"))
                                .withFrom(parse("1980-01-01"))
                                .withTo(parse("2013-01-01"))
                                .withVerifiedStatus(true)
                                .build()
                )
                .withAddress(
                        AddressBuilder.anAddress()
                                .withFromDate(parse("2001-01-01"))
                                .withToDate(parse("2012-12-12"))
                                .withVerified(false)
                                .withPostCode("WC2B 6NH")
                                .withLines(
                                        asList(
                                                "Aviation House",
                                                "London"
                                        )
                                )
                                .build()
                )
                .withLevelOfAssurance("LEVEL_2")
                .build();


        assertThat(compareIdpUserDto(idpUserDtoFromJson,idpuserDto)).isTrue();


    }

    private boolean compareIdpUserDto(final IdpUserDto idpUserDtoFromJson, final IdpUserDto idpuserDto) {

        assertThat(idpuserDto.getPid()).isEqualTo(idpUserDtoFromJson.getPid());

        assertThat(idpuserDto.getUsername()).isEqualTo(idpUserDtoFromJson.getUsername());

        assertThat(idpuserDto.getPassword()).isEqualTo(idpUserDtoFromJson.getPassword());

        assertThat(idpuserDto.getFirstName().isPresent()).isTrue();
        assertThat(idpUserDtoFromJson.getFirstName().isPresent()).isTrue();
        SimpleMdsValue<String> idpuserDtoFirstname = idpuserDto.getFirstName().get();
        SimpleMdsValue<String> idpUserDtoFromJsonFirstname = idpUserDtoFromJson.getFirstName().get();
        compareSimpleMdsObjects(idpuserDtoFirstname,idpUserDtoFromJsonFirstname);

        assertThat(idpuserDto.getMiddleNames().isPresent()).isTrue();
        assertThat(idpUserDtoFromJson.getMiddleNames().isPresent()).isTrue();
        SimpleMdsValue<String> idpUserDtoMiddleNames = idpuserDto.getMiddleNames().get();
        SimpleMdsValue<String> idpUserDtoFromJsonMiddleNames = idpUserDtoFromJson.getMiddleNames().get();
        compareSimpleMdsObjects(idpUserDtoMiddleNames,idpUserDtoFromJsonMiddleNames);


        assertThat(idpuserDto.getSurnames().size()).isEqualTo(2);
        assertThat(idpUserDtoFromJson.getSurnames().size()).isEqualTo(2);
        SimpleMdsValue<String> idpUserDtoFirstSurname = idpuserDto.getSurnames().get(0);
        SimpleMdsValue<String> idpUserDtoFromJsonFirstSurname = idpUserDtoFromJson.getSurnames().get(0);
        compareSimpleMdsObjects(idpUserDtoFirstSurname,idpUserDtoFromJsonFirstSurname);

        SimpleMdsValue<String> idpUserDtoSecondSurname = idpuserDto.getSurnames().get(1);
        SimpleMdsValue<String> idpUserFromJsonDtoSecondSurname = idpUserDtoFromJson.getSurnames().get(1);
        compareSimpleMdsObjects(idpUserDtoSecondSurname,idpUserFromJsonDtoSecondSurname);

        assertThat(idpuserDto.getGender().isPresent()).isTrue();
        assertThat(idpUserDtoFromJson.getGender().isPresent()).isTrue();
        SimpleMdsValue<Gender> idpuserDtoGender = idpuserDto.getGender().get();
        SimpleMdsValue<Gender> idpUserDtoFromJsonGender = idpUserDtoFromJson.getGender().get();
        compareSimpleMdsObjects(idpuserDtoGender,idpUserDtoFromJsonGender);

        assertThat(idpuserDto.getDateOfBirth().isPresent()).isTrue();
        assertThat(idpUserDtoFromJson.getDateOfBirth().isPresent()).isTrue();
        SimpleMdsValue<LocalDate> idpuserDtoDateOfBirth = idpuserDto.getDateOfBirth().get();
        SimpleMdsValue<LocalDate> idpUserDtoFromJsonDateOfBirth = idpUserDtoFromJson.getDateOfBirth().get();
        compareSimpleMdsObjects(idpuserDtoDateOfBirth,idpUserDtoFromJsonDateOfBirth);


        assertThat(idpuserDto.getAddress().isPresent()).isTrue();
        assertThat(idpUserDtoFromJson.getAddress().isPresent()).isTrue();
        Address idpuserDtoAddress = idpuserDto.getAddress().get();
        Address idpUserDtoFromJsonAddress = idpUserDtoFromJson.getAddress().get();
        assertThat(idpuserDtoAddress.getFrom().toString()).isEqualTo(idpUserDtoFromJsonAddress.getFrom().toString());
        assertThat(idpuserDtoAddress.getTo().toString()).isEqualTo(idpUserDtoFromJsonAddress.getTo().toString());
        assertThat(idpuserDtoAddress.getInternationalPostCode()).isEqualTo(idpUserDtoFromJsonAddress.getInternationalPostCode());
        assertThat(idpuserDtoAddress.getLines()).isEqualTo(idpUserDtoFromJsonAddress.getLines());
        assertThat(idpuserDtoAddress.getPostCode()).isEqualTo(idpUserDtoFromJsonAddress.getPostCode());
        assertThat(idpuserDtoAddress.getUPRN()).isEqualTo(idpUserDtoFromJsonAddress.getUPRN());
        assertThat(idpuserDtoAddress.isVerified()).isEqualTo(idpUserDtoFromJsonAddress.isVerified());

        assertThat(idpuserDto.getLevelOfAssurance()).isEqualTo(idpUserDtoFromJson.getLevelOfAssurance());

        return true;
    }

    private <T> void compareSimpleMdsObjects(final SimpleMdsValue<T> firstSimpleMdsValue, final SimpleMdsValue<T> secondSimpleMdsValue) {
        Assertions.assertThat(firstSimpleMdsValue.getValue()).isEqualTo(secondSimpleMdsValue.getValue());
        assertThat(firstSimpleMdsValue.getFrom().toString()).isEqualTo(secondSimpleMdsValue.getFrom().toString());
        assertThat(firstSimpleMdsValue.getTo().toString()).isEqualTo(secondSimpleMdsValue.getTo().toString());
        Assertions.assertThat(firstSimpleMdsValue.isVerified()).isEqualTo(secondSimpleMdsValue.isVerified());
    }


    private SimpleMdsValue<String> createSimpleMdsValue(String value) {
        return SimpleMdsValueBuilder.<String>aSimpleMdsValue()
                .withValue(value)
                .withFrom(parse("1980-01-01"))
                .withTo(parse("2013-01-01"))
                .withVerifiedStatus(true)
                .build();
    }

}
