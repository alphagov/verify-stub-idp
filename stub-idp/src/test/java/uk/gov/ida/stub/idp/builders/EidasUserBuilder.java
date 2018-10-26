package uk.gov.ida.stub.idp.builders;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static uk.gov.ida.saml.core.domain.AuthnContext.LEVEL_1;

public class EidasUserBuilder {

    private String username = "default-username";
    private String persistentId = "default-persistent-id";
    private String password = "default-password";
    private List<Address> addresses = emptyList();
    private AuthnContext levelOfAssurance = LEVEL_1;
    private MatchingDatasetValue<String> firstName = new MatchingDatasetValue<>(
            "default-first-name",
            DateTime.now().minusYears(20),
            null,
            true
    );
    private MatchingDatasetValue<String> firstNameNonLatin = new MatchingDatasetValue<>(
            "default-first-name-non-latin",
            DateTime.now().minusYears(20),
            null,
            true
    );
    private MatchingDatasetValue<String> familyName = new MatchingDatasetValue<>(
            "default-family-name",
            DateTime.now().minusYears(20),
            null,
            true
    );
    private MatchingDatasetValue<String> familyNameNonLatin = new MatchingDatasetValue<>(
            "default-family-name-non-latin",
            DateTime.now().minusYears(20),
            null,
            true
    );
    private MatchingDatasetValue<LocalDate> dateOfBirth = new MatchingDatasetValue<>(
            LocalDate.now().minusYears(20),
            DateTime.now().minusYears(20),
            null,
            true
    );

    public static EidasUserBuilder anEidasUser() {
        return new EidasUserBuilder();
    }

    public DatabaseEidasUser build() {
        return new DatabaseEidasUser(
                username,
                persistentId,
                password,
                firstName,
                Optional.of(firstNameNonLatin),
                familyName,
                Optional.of(familyNameNonLatin),
                dateOfBirth,
                levelOfAssurance
        );
    }

    public EidasUserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public EidasUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }
}
