package uk.gov.ida.stub.idp.repositories;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Optional.fromNullable;

public final class HardCodedTestUserList {

    private HardCodedTestUserList() {}

    public static List<IdpUser> getHardCodedTestUsers(String idpFriendlyId) {

        List<IdpUser> sacredUsers = new ArrayList<>();

        sacredUsers.add(new IdpUser(
                idpFriendlyId,
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.singletonList(createMdsValue("Cornelius")),
                Collections.singletonList(createMdsValue("Bauer")),
                fromNullable(createMdsValue(Gender.MALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1984-02-29"))),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", null, null, null, null, true),
                        new AddressFactory().create(Collections.singletonList("221b Baker St."), "W4 1SH", null, null, DateTime.parse("2007-09-27"), DateTime.parse("2007-09-28"), true),
                        new AddressFactory().create(Collections.singletonList("1 Goose Lane"), "M1 2FG", null, null, DateTime.parse("2006-09-29"), DateTime.parse("2006-09-8"), false)
                ),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-other",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Martin")),
                Collections.singletonList(createMdsValue("Seamus")),
                Collections.singletonList(createMdsValue("McFly")),
                fromNullable(createMdsValue(Gender.FEMALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1968-06-12"))),
                Collections.singletonList(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", null, null, null, null, true)),
                AuthnContext.LEVEL_2));


        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-new",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("Jack")),
                Collections.emptyList(),
                Collections.singletonList(createMdsValue("Griffin")),
                fromNullable(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1983-06-21"))),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("Lion's Head Inn"), "1A 2BC", null, null, DateTime.now().minusYears(1), null, true),
                        new AddressFactory().create(Collections.singletonList("Ye Olde Inn"), "1A 2BB", null, null, DateTime.now().minusYears(3), DateTime.now().minusYears(1), false)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-c3",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("J")),
                Collections.emptyList(),   //No middle names that we could find. :)
                ImmutableList.of(createMdsValue("Moriarti"), new SimpleMdsValue<>("Barnes", DateTime.parse("2006-09-29"), DateTime.parse("2006-09-8"), true)),
                fromNullable(createMdsValue(Gender.NOT_SPECIFIED)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1822-11-27"))),
                Collections.singletonList(new AddressFactory().create(Collections.singletonList("10 Two St"), "1A 2BC", null, null, null, null, true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-complete",
                UUID.randomUUID().toString(),
                "bar",
                ImmutableList.of(new SimpleMdsValue<>("Jack", DateTime.now(), DateTime.now(), true),
                        new SimpleMdsValue<>("Spud", DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new SimpleMdsValue<>("Cornelius", DateTime.now(), DateTime.now(), true),
                        new SimpleMdsValue<>("Aurelius", DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new SimpleMdsValue<>("Bauer", DateTime.now(), DateTime.now(), true),
                        new SimpleMdsValue<>("Superman", DateTime.now().minusDays(5), DateTime.now().minusDays(3), true)),
                fromNullable(new SimpleMdsValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new SimpleMdsValue<>(LocalDate.parse("1984-02-29"), DateTime.now(), DateTime.now(), true),
                        new SimpleMdsValue<>(LocalDate.parse("1984-03-01"), DateTime.now(), DateTime.now(), true)),
                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-loa1",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new SimpleMdsValue<>("Jessica", DateTime.now(), null, false)),
                Collections.singletonList(new SimpleMdsValue<>("", DateTime.now(), null, false)),
                Collections.singletonList(new SimpleMdsValue<>("Rabbit", DateTime.now(), null, false)),
                fromNullable(new SimpleMdsValue<>(Gender.FEMALE, DateTime.now(), null, false)),
                Collections.singletonList(new SimpleMdsValue<>(LocalDate.parse("1960-03-23"), DateTime.now(), null, false)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), null, false),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), false)),
                AuthnContext.LEVEL_1));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-loa2",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new SimpleMdsValue<>("Roger", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>("", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>("Rabbit", DateTime.now(), DateTime.now(), true)),
                fromNullable(new SimpleMdsValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_2));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-loa3",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new SimpleMdsValue<>("Apollo", DateTime.now(), null, true)),
                Collections.singletonList(new SimpleMdsValue<>("", DateTime.now(), null, true)),
                Collections.singletonList(new SimpleMdsValue<>("Eagle", DateTime.now(), null, true)),
                fromNullable(new SimpleMdsValue<>(Gender.FEMALE, DateTime.now(), null, true)),
                Collections.singletonList(new SimpleMdsValue<>(LocalDate.parse("1969-07-20"), DateTime.now(), null, true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Four St"), "1A 2BD", "Something", "dummy uprn", DateTime.now(), null, true),
                        new AddressFactory().create(Collections.singletonList("2 Five St"), "1B 2RD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_3));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-loax",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(new SimpleMdsValue<>("Bugs", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>("", DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>("Nummy", DateTime.now(), DateTime.now(), true)),
                fromNullable(new SimpleMdsValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
                Collections.singletonList(new SimpleMdsValue<>(LocalDate.parse("1958-04-09"), DateTime.now(), DateTime.now(), true)),

                ImmutableList.of(new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), DateTime.now(), true),
                        new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true)),
                AuthnContext.LEVEL_X));

        sacredUsers.add(new IdpUser(
                idpFriendlyId + "-emoji",
                UUID.randomUUID().toString(),
                "bar",
                Collections.singletonList(createMdsValue("😀")),
                Collections.singletonList(createMdsValue("😎")),
                Collections.singletonList(createMdsValue("🙃")),
                fromNullable(createMdsValue(Gender.FEMALE)),
                Collections.singletonList(createMdsValue(LocalDate.parse("1968-06-12"))),
                Collections.singletonList(new AddressFactory().create(ImmutableList.of("🏠"), "🏘", null, null, null, null, true)),
                AuthnContext.LEVEL_2));

        return sacredUsers;
    }

    private static <T> SimpleMdsValue<T> createMdsValue(T value) {
        if (value == null) {
            return null;
        }

        return new SimpleMdsValue<>(value, null, null, true);
    }
}
