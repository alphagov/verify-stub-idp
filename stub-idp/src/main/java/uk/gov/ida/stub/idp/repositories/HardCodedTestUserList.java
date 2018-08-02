package uk.gov.ida.stub.idp.repositories;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class HardCodedTestUserList {

    private HardCodedTestUserList() {
    }

    public static List<DatabaseIdpUser> getHardCodedTestUsers(String idpFriendlyId) {

        List<DatabaseIdpUser> sacredUsers = new ArrayList<>();

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId,
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.singletonList(createMdsValue("Cornelius")),
                Collections.singletonList(createMdsValue("Bauer")),
                Optional.ofNullable(createMdsValue(Gender.MALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1984-02-29"))),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", null, null, null, null, true),
                        new AddressFactory().create(Collections.singletonList("221b Baker St."), "W4 1SH", null, null, DateTime.parse("2007-09-27"), DateTime.parse("2007-09-28"), true),
                        new AddressFactory().create(Collections.singletonList("1 Goose Lane"), "M1 2FG", null, null, DateTime.parse("2006-09-29"), DateTime.parse("2006-09-8"), false)
                ),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-other",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Martin")),
                Collections.singletonList(createMdsValue("Seamus")),
                Collections.singletonList(createMdsValue("McFly")),
                Optional.of(createMdsValue(Gender.FEMALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1968-06-12"))),
                Collections.singletonList(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", null, null, null, null, true)),
                AuthnContext.LEVEL_2));


        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-new",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Griffin")),
                Optional.of(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1983-06-21"))),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("Lion's Head Inn"), "1A 2BC", null, null, DateTime.now().minusYears(1), null, true),
                        new AddressFactory().create(Collections.singletonList("Ye Olde Inn"), "1A 2BB", null, null, DateTime.now().minusYears(3), DateTime.now().minusYears(1), false)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-c3",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("J")),
                Collections.emptyList(),   //No middle names that we could find. :)
                ImmutableList.of(createMdsValue("Moriarti"), new MatchingDatasetValue<>("Barnes", DateTime.parse("2006-09-29"), DateTime.parse("2006-09-8"), true)),
                Optional.of(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1822-11-27"))),
                Collections.singletonList(new AddressFactory().create(Collections.singletonList("10 Two St"), "1A 2BC", null, null, null, null, true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-ec3",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Martin")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Riggs")),
                Optional.empty(),
                Collections.singletonList(createMdsValue(LocalDate.parse("1970-04-12"))),
                Collections.emptyList(),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-complete",
                UUID.randomUUID().toString(),
                "bar",
                ImmutableList.of(new MatchingDatasetValue<>("Jack", DateTime.now(), DateTime.now(), true),
                        new MatchingDatasetValue<>("Spud", DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new MatchingDatasetValue<>("Cornelius", DateTime.now(), DateTime.now(), true),
                        new MatchingDatasetValue<>("Aurelius", DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new MatchingDatasetValue<>("Bauer", DateTime.now(), DateTime.now(), true),
                        new MatchingDatasetValue<>("Superman", DateTime.now().minusDays(5), DateTime.now().minusDays(3), true)),
                Optional.of(new MatchingDatasetValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new MatchingDatasetValue<>(LocalDate.parse("1984-02-29"), DateTime.now(), DateTime.now(), true),
                        new MatchingDatasetValue<>(LocalDate.parse("1984-03-01"), DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-loa1",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new MatchingDatasetValue<>("Jessica", DateTime.now(), null, false)),
                Collections.singletonList(new MatchingDatasetValue<>("", DateTime.now(), null, false)),
                Collections.singletonList(new MatchingDatasetValue<>("Rabbit", DateTime.now(), null, false)),
                Optional.of(new MatchingDatasetValue<>(Gender.FEMALE, DateTime.now(), null, false)),
                Collections.singletonList(new MatchingDatasetValue<>(LocalDate.parse("1960-03-23"), DateTime.now(), null, false)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), null, false),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), false)),
                AuthnContext.LEVEL_1));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-loa2",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new MatchingDatasetValue<>("Roger", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>("", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>("Rabbit", DateTime.now(), DateTime.now(), true)),
                Optional.of(new MatchingDatasetValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-loa3",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new MatchingDatasetValue<>("Apollo", DateTime.now(), null, true)),
                Collections.singletonList(new MatchingDatasetValue<>("", DateTime.now(), null, true)),
                Collections.singletonList(new MatchingDatasetValue<>("Eagle", DateTime.now(), null, true)),
                Optional.of(new MatchingDatasetValue<>(Gender.FEMALE, DateTime.now(), null, true)),
                Collections.singletonList(new MatchingDatasetValue<>(LocalDate.parse("1969-07-20"), DateTime.now(), null, true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Four St"), "1A 2BD", "Something", "dummy uprn", DateTime.now(), null, true),
                        new AddressFactory().create(Collections.singletonList("2 Five St"), "1B 2RD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_3));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-loax",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new MatchingDatasetValue<>("Bugs", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>("", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>("Nummy", DateTime.now(), DateTime.now(), true)),
                Optional.of(new MatchingDatasetValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new MatchingDatasetValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_X));

        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-emoji",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("😀")),
                Collections.singletonList(createMdsValue("😎")),
                Collections.singletonList(createMdsValue("🙃")),
                Optional.of(createMdsValue(Gender.FEMALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1968-06-12"))),
                Collections.singletonList(new AddressFactory().create(ImmutableList.of("🏠"), "🏘", null, null, null, null, true)),
                AuthnContext.LEVEL_2));

        // this user matches one user in the example local matching service
        // https://github.com/alphagov/verify-local-matching-service-example/blob/b135523be4c156b5f6e4fc0b3b3f94bcfbef9f75/src/main/resources/db/migration/V2__Populate_With_Test_Data.sql#L31
        sacredUsers.add(new DatabaseIdpUser(
                idpFriendlyId + "-elms",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Joe")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Bloggs")),
                Optional.of(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1970-01-01"))),
                ImmutableList.of(new AddressFactory().create(ImmutableList.of("The White Chapel Building, 10 Whitechapel High St", "London", "United Kingdom"), "E1 8DX",
                        null, null, DateTime.now().minusYears(1), null, true)),
                AuthnContext.LEVEL_2));

        return sacredUsers;
    }

    public static List<DatabaseEidasUser> getHardCodedCountryTestUsers(String idpFriendlyId) {

        List<DatabaseEidasUser> sacredUsers = new ArrayList<>();

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId,
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Jack"),
                Optional.empty(),
                createMdsValue("Bauer"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1984-02-29")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-other",
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Martin"),
                Optional.empty(),
                createMdsValue("McFly"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1968-06-12")),
                AuthnContext.LEVEL_2));

        // These names contain characters from ISO/IEC 8859-15 which we regard as Latin.
        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-accents",
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Šarlota"),
                Optional.empty(),
                createMdsValue("Snježana"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1978-06-12")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-nonlatin",
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Georgios"),
                Optional.of(createMdsValue("Γεώργιος")),
                createMdsValue("Panathinaikos"),
                Optional.of(createMdsValue("Παναθηναϊκός")),
                createMdsValue(LocalDate.parse("1967-06-12")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-new",
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Jack"),
                Optional.empty(),
                createMdsValue("Griffin"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1983-06-21")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-c3",
                UUID.randomUUID().toString(), "bar",
                createMdsValue("J"),
                Optional.empty(),
                createMdsValue("Surname"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1822-11-27")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-ec3",
                 UUID.randomUUID().toString(),
                "bar",
                createMdsValue("Martin"),
                Optional.empty(),
                createMdsValue("Riggs"),
                Optional.empty(),
                createMdsValue(LocalDate.parse("1970-04-12")),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-complete",
                UUID.randomUUID().toString(),
                "bar",
                new MatchingDatasetValue<>("Jack", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>("Bauer", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>(LocalDate.parse("1984-02-29"), DateTime.now(), DateTime.now(), true),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-loa1",
                UUID.randomUUID().toString(),
                "bar",
                new MatchingDatasetValue<>("Jessica", DateTime.now(), null, false),
                Optional.empty(),
                new MatchingDatasetValue<>("Rabbit", DateTime.now(), null, false),
                Optional.empty(),
                new MatchingDatasetValue<>(LocalDate.parse("1960-03-23"), DateTime.now(), null, false),
                AuthnContext.LEVEL_1));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-loa2",
                UUID.randomUUID().toString(),
                "bar",
                new MatchingDatasetValue<>("Roger", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>("Rabbit", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-loa3",
                UUID.randomUUID().toString(),
                "bar",
                new MatchingDatasetValue<>("Apollo", DateTime.now(), null, true),
                Optional.empty(),
                new MatchingDatasetValue<>("Eagle", DateTime.now(), null, true),
                Optional.empty(),
                new MatchingDatasetValue<>(LocalDate.parse("1969-07-20"), DateTime.now(), null, true),
                AuthnContext.LEVEL_3));

        sacredUsers.add(new DatabaseEidasUser(
                idpFriendlyId + "-loax",
                UUID.randomUUID().toString(),
                "bar",
                new MatchingDatasetValue<>("Bugs", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>("Nummy", DateTime.now(), DateTime.now(), true),
                Optional.empty(),
                new MatchingDatasetValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true),
                AuthnContext.LEVEL_X));

        sacredUsers.add(new DatabaseEidasUser(idpFriendlyId + "-emoji",
                UUID.randomUUID().toString(),
                "bar",
                createMdsValue("😀"),
                Optional.of(createMdsValue("GRINNING FACE")),
                createMdsValue("🙃"),
                Optional.of(createMdsValue("UPSIDE-DOWN FACE")),
                createMdsValue(LocalDate.parse("1968-06-12")),
                AuthnContext.LEVEL_2));

        return sacredUsers;
    }

    private static <T> MatchingDatasetValue<T> createMdsValue(T value) {
        if (value == null) {
            return null;
        }

        return new MatchingDatasetValue<>(value, null, null, true);
    }
}
