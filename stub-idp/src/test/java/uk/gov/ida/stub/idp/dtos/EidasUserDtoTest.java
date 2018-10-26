package uk.gov.ida.stub.idp.dtos;

import io.dropwizard.jackson.Jackson;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import uk.gov.ida.stub.idp.builders.SimpleMdsValueBuilder;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.EidasUserDtoBuilder.anEidasUserDto;

public class EidasUserDtoTest {

    @Test
    public void shouldDeSerialiseJsonToObjectWhenAllFieldsArePopulated() throws IOException {

        EidasUserDto eidasUserDtoFromJson = Jackson.newObjectMapper().readValue("{\"pid\":\"00754148-902f-4d94-b0db-cb1f7eb3fd84\",\"username\":\"user1\",\"password\":\"password\",\"firstName\":{\"value\":\"Georgios\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"firstNameNonLatin\":{\"value\":\"Γεώργιος\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"surname\":{\"value\":\"Panathinaikos\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"surnameNonLatin\":{\"value\":\"Παναθηναϊκός\",\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"dateOfBirth\":{\"value\":[1970,1,1],\"from\":315532800000,\"to\":1356998400000,\"verified\":true},\"levelOfAssurance\":\"LEVEL_2\"}", EidasUserDto.class);

        EidasUserDto eidasUserDto = anEidasUserDto()
                .withPid("00754148-902f-4d94-b0db-cb1f7eb3fd84")
                .withUserName("user1")
                .withPassword("password")
                .withFirsName(createSimpleMdsValue("Georgios"))
                .withFirstNameNonLatin(createSimpleMdsValue("Γεώργιος"))
                .withFamilyName(createSimpleMdsValue("Panathinaikos"))
                .withFamilyNameNonLatin(createSimpleMdsValue("Παναθηναϊκός"))
                .withDateOfBirth(
                        SimpleMdsValueBuilder.<LocalDate>aSimpleMdsValue()
                                .withValue(new LocalDate(1970, 1, 1))
                                .withFrom(new DateTime(1980, 1, 1, 0, 0, 0, DateTimeZone.UTC))
                                .withTo(new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC))
                                .withVerifiedStatus(true)
                                .build()
                )
                .withLevelOfAssurance("LEVEL_2")
                .build();

        assertThat(compareIdpUserDto(eidasUserDtoFromJson, eidasUserDto)).isTrue();
    }

    private boolean compareIdpUserDto(final EidasUserDto eidasUserDtoFromJson, final EidasUserDto eidasUserDto) {

        assertThat(eidasUserDto.getPid()).isEqualTo(eidasUserDtoFromJson.getPid());

        assertThat(eidasUserDto.getUsername()).isEqualTo(eidasUserDtoFromJson.getUsername());

        assertThat(eidasUserDto.getPassword()).isEqualTo(eidasUserDtoFromJson.getPassword());

        MatchingDatasetValue<String> eidasUserDtoFirstName = eidasUserDto.getFirstName();
        MatchingDatasetValue<String> eidasUserDtoFromJsonFirstName = eidasUserDtoFromJson.getFirstName();
        compareSimpleMdsObjects(eidasUserDtoFirstName, eidasUserDtoFromJsonFirstName);

        assertThat(eidasUserDto.getFirstNameNonLatin().isPresent()).isTrue();
        assertThat(eidasUserDtoFromJson.getFirstNameNonLatin().isPresent()).isTrue();
        MatchingDatasetValue<String> eidasUserDtoFirstNameNonLatin = eidasUserDto.getFirstNameNonLatin().get();
        MatchingDatasetValue<String> eidasUserDtoFromJsonFirstNameNonLatin = eidasUserDtoFromJson.getFirstNameNonLatin().get();
        compareSimpleMdsObjects(eidasUserDtoFirstNameNonLatin, eidasUserDtoFromJsonFirstNameNonLatin);

        MatchingDatasetValue<String> eidasUserDtoFamilyName = eidasUserDto.getFamilyName();
        MatchingDatasetValue<String> eidasUserDtoFromJsonFamilyName = eidasUserDtoFromJson.getFamilyName();
        compareSimpleMdsObjects(eidasUserDtoFamilyName, eidasUserDtoFromJsonFamilyName);

        assertThat(eidasUserDto.getFamilyNameNonLatin().isPresent()).isTrue();
        assertThat(eidasUserDtoFromJson.getFamilyNameNonLatin().isPresent()).isTrue();
        MatchingDatasetValue<String> eidasUserDtoFamilyNameNonLatin = eidasUserDto.getFamilyNameNonLatin().get();
        MatchingDatasetValue<String> eidasUserDtoFromJsonFamilyNameNonLatin = eidasUserDtoFromJson.getFamilyNameNonLatin().get();
        compareSimpleMdsObjects(eidasUserDtoFamilyNameNonLatin, eidasUserDtoFromJsonFamilyNameNonLatin);

        MatchingDatasetValue<LocalDate> eidasUserDtoDateOfBirth = eidasUserDto.getDateOfBirth();
        MatchingDatasetValue<LocalDate> eidasUserDtoFromJsonDateOfBirth = eidasUserDtoFromJson.getDateOfBirth();
        compareSimpleMdsObjects(eidasUserDtoDateOfBirth, eidasUserDtoFromJsonDateOfBirth);

        assertThat(eidasUserDto.getLevelOfAssurance()).isEqualTo(eidasUserDtoFromJson.getLevelOfAssurance());

        return true;
    }

    private <T> void compareSimpleMdsObjects(final MatchingDatasetValue<T> firstSimpleMdsValue, final MatchingDatasetValue<T> secondSimpleMdsValue) {
        Assertions.assertThat(firstSimpleMdsValue.getValue()).isEqualTo(secondSimpleMdsValue.getValue());
        assertThat(firstSimpleMdsValue.getFrom().toString()).isEqualTo(secondSimpleMdsValue.getFrom().toString());
        assertThat(firstSimpleMdsValue.getTo().toString()).isEqualTo(secondSimpleMdsValue.getTo().toString());
        Assertions.assertThat(firstSimpleMdsValue.isVerified()).isEqualTo(secondSimpleMdsValue.isVerified());
    }


    private MatchingDatasetValue<String> createSimpleMdsValue(String value) {
        return SimpleMdsValueBuilder.<String>aSimpleMdsValue()
                .withValue(value)
                .withFrom(new DateTime(1980, 1, 1, 0, 0, 0, DateTimeZone.UTC))
                .withTo(new DateTime(2013, 1, 1, 0, 0, 0, DateTimeZone.UTC))
                .withVerifiedStatus(true)
                .build();
    }
}
