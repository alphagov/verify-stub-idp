package uk.gov.ida.stub.idp.repositories.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.jdbi.v3.core.Jdbi;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.stub.idp.domain.DatabaseEidasUser;
import uk.gov.ida.stub.idp.domain.DatabaseIdpUser;
import uk.gov.ida.stub.idp.domain.MatchingDatasetValue;
import uk.gov.ida.stub.idp.repositories.jdbc.migrations.DatabaseMigrationRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.EidasUserBuilder.anEidasUser;
import static uk.gov.ida.stub.idp.builders.IdpUserBuilder.anIdpUser;

public class JDBIUserRepositoryTest {

    private Jdbi jdbi;
    private JDBIUserRepository repository;
    private UserMapper userMapper;
    // Unfortunately H2 does not support JSON type and we need to do things differently inmemory
    private boolean isPostgresDb = false;

    @Before
    public void setUp() {
        String url = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
        jdbi = Jdbi.create(url);
        new DatabaseMigrationRunner().runMigration(url);

        ObjectMapper mapper = new ObjectMapper() {{
            registerModule(new JodaModule());
            registerModule(new GuavaModule());
            registerModule(new Jdk8Module());
        }};

        userMapper = new UserMapper(mapper);

        repository = new JDBIUserRepository(jdbi, userMapper);
    }

    @Test
    public void addOrUpdateUserForIdpShouldAddRecordIfUserDoesNotExist() {
        ensureNoUserExistsFor("some-idp-friendly-id");

        DatabaseIdpUser idpUser = anIdpUser()
            .withUsername("some-username")
            .build();

        repository.addOrUpdateUserForIdp("some-idp-friendly-id", idpUser);

        List<DatabaseIdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(1);
        assertThat(idpUsers.get(0)).isEqualTo(idpUser);
    }

    @Test
    public void addOrUpdateUserForIdpShouldUpdateRecordIfUserAlreadyExists() {
        DatabaseIdpUser someUser = anIdpUser()
            .withUsername("some-username")
            .withPassword("some-password")
            .build();

        ensureNoUserExistsFor("some-idp-friendly-id");
        ensureUserExistsFor("some-idp-friendly-id", someUser);

        DatabaseIdpUser sameUserDifferentPassword = anIdpUser()
            .withUsername("some-username")
            .withPassword("another-password")
            .build();

        repository.addOrUpdateUserForIdp("some-idp-friendly-id", sameUserDifferentPassword);

        List<DatabaseIdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(1);
        assertThat(idpUsers.get(0)).isEqualTo(sameUserDifferentPassword);
    }

    @Test
    public void deleteUserFromIdpShouldDeleteGivenUserFromGivenIdp() {
        DatabaseIdpUser someUser = anIdpUser()
            .withUsername("some-username")
            .build();

        ensureUserExistsFor("some-idp-friendly-id", someUser);

        repository.deleteUserFromIdp("some-idp-friendly-id", "some-username");

        List<DatabaseIdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(0);
    }

    @Test
    public void deleteEidasUserFromStubCountryShouldDeleteGivenUserFromGivenCountry() {
        DatabaseEidasUser eidasUser = anEidasUser()
                .withUsername("some-username")
                .build();

        ensureEidasUserExistsFor("some-country-friendly-id", eidasUser);

        repository.deleteUserFromIdp("some-country-friendly-id", "some-username");

        List<DatabaseEidasUser> eidasUsers = new ArrayList<>(repository.getUsersForCountry("some-country-friendly-id"));

        assertThat(eidasUsers).size().isEqualTo(0);
    }

    @Test
    public void getUsersForIdpShouldReturnAllUsersForGivenIdp() {
        ensureNoUserExistsFor("some-idp-friendly-id");

        DatabaseIdpUser firstUser = anIdpUser().withUsername("first-username").build();
        DatabaseIdpUser secondUser = anIdpUser().withUsername("second-username").build();

        ensureUserExistsFor("some-idp-friendly-id", firstUser);
        ensureUserExistsFor("some-idp-friendly-id", secondUser);

        List<DatabaseIdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(2);
    }

    @Test
    public void addOrUpdateUserForStubCountryShouldAddRecordIfUserDoesNotExist(){
        ensureNoUserExistsFor("stub-country-friendly-id");

        DatabaseEidasUser eidasUser = new DatabaseEidasUser("some-username", null, "some-password", createMdsValue("firstName"), Optional.of(createMdsValue("firstNameNonLatin")), createMdsValue("surname"), Optional.of(createMdsValue("surnameNonLatin")), createMdsValue(LocalDate.now()), AuthnContext.LEVEL_2);

        repository.addOrUpdateEidasUserForStubCountry("stub-country-friendly-id", eidasUser);

        Collection<DatabaseEidasUser> users = repository.getUsersForCountry("stub-country-friendly-id");

        assertThat(users).size().isEqualTo(1);
        assertThat(users).contains(eidasUser);

    }

    private void ensureNoUserExistsFor(String idpFriendlyId) {
        jdbi.withHandle(handle ->
            handle.createUpdate("DELETE FROM users WHERE identity_provider_friendly_id = :idpFriendlyId")
                .bind("idpFriendlyId", idpFriendlyId)
                .execute()
        );
    }

    private void ensureUserExistsFor(String idpFriendlyId, DatabaseIdpUser idpUser) {
        repository.deleteUserFromIdp(idpFriendlyId, idpUser.getUsername());

        User user = userMapper.mapFrom(idpFriendlyId, idpUser);

        jdbi.withHandle(handle -> {
                String sqlStatementParameter = isPostgresDb ? "to_json(:json)" : ":json";

                String sqlStatement = String.format("INSERT INTO users(username, password, identity_provider_friendly_id, \"data\") " +
                    "VALUES (:username, :password, :idpFriendlyId, %s)", sqlStatementParameter);

                String userData = isPostgresDb ? user.getData() : "\"" + escapeJson(user.getData()) + "\"";

                return handle.createUpdate(sqlStatement)
                    .bind("username", user.getUsername())
                    .bind("password", user.getPassword())
                    .bind("idpFriendlyId", idpFriendlyId)
                    .bind("json", userData)
                    .execute();
            }
        );
    }

    private void ensureEidasUserExistsFor(String countryFriendlyId, DatabaseEidasUser eidasUser) {
        repository.deleteEidasUserFromStubCountry(countryFriendlyId, eidasUser.getUsername());

        User user = userMapper.mapFrom(countryFriendlyId, eidasUser);

        jdbi.withHandle(handle -> {
                    String sqlStatementParameter = isPostgresDb ? "to_json(:json)" : ":json";

                    String sqlStatement = String.format("INSERT INTO users(username, password, identity_provider_friendly_id, \"data\") " +
                            "VALUES (:username, :password, :countryFriendlyId, %s)", sqlStatementParameter);

                    String userData = isPostgresDb ? user.getData() : "\"" + escapeJson(user.getData()) + "\"";

                    return handle.createUpdate(sqlStatement)
                            .bind("username", user.getUsername())
                            .bind("password", user.getPassword())
                            .bind("countryFriendlyId", countryFriendlyId)
                            .bind("json", userData)
                            .execute();
                }
        );
    }

    private <T> MatchingDatasetValue<T> createMdsValue(T value) {
        return new MatchingDatasetValue<>(value, null, null, true);
    }
}
