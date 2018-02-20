package uk.gov.ida.stub.idp.domain.factories;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AddressFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.Gender;
import uk.gov.ida.saml.core.domain.MatchingDataset;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.Collections;
import java.util.UUID;

import static com.google.common.base.Optional.fromNullable;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MatchingDatasetFactoryTest {

    private static final Address currentAddress = new AddressFactory().create(Collections.singletonList("1 Two St"), "1A 2BC", "Something", "dummy uprn", DateTime.now(), null, true);
    private static final Address previousAddress = new AddressFactory().create(Collections.singletonList("2 Three St"), "1B 2CD", "Something else", "dummy second uprn", DateTime.now(), DateTime.now(), true);
    public static final IdpUser completeUser = new IdpUser(
            "idpuser-complete",
            UUID.randomUUID().toString(),
    "bar",
    asList(new SimpleMdsValue<>("Jack", DateTime.now(), DateTime.now(), true), new SimpleMdsValue<>("Spud", DateTime.now(), DateTime.now(), true)),
    asList(new SimpleMdsValue<>("Cornelius", DateTime.now(), DateTime.now(), true), new SimpleMdsValue<>("Aurelius", DateTime.now(), DateTime.now(), true)),
    asList(new SimpleMdsValue<>("Bauer", DateTime.now(), DateTime.now(), true), new SimpleMdsValue<>("Superman", DateTime.now().minusDays(5), DateTime.now().minusDays(3), true)),
    fromNullable(new SimpleMdsValue<>(Gender.MALE, DateTime.now(), DateTime.now(), true)),
    asList(new SimpleMdsValue<>(LocalDate.parse("1984-02-29"), DateTime.now(), DateTime.now(), true), new SimpleMdsValue<>(LocalDate.parse("1984-03-01"), DateTime.now(), DateTime.now(), true)),
    asList(previousAddress,
            currentAddress),
    AuthnContext.LEVEL_2);

    @Test
    public void shouldSplitAddressesIntoCurrentAndPrevious() {

        final MatchingDataset matchingDataset = MatchingDatasetFactory.create(completeUser);
        assertThat(matchingDataset.getCurrentAddresses().size()).isEqualTo(1);
        assertThat(matchingDataset.getPreviousAddresses().size()).isEqualTo(1);
        assertThat(matchingDataset.getCurrentAddresses().get(0)).isEqualTo(currentAddress);
        assertThat(matchingDataset.getPreviousAddresses().get(0)).isEqualTo(previousAddress);

    }


}