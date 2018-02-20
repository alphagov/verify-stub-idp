package uk.gov.ida.stub.idp.repositories.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeJson;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.stub.idp.builders.IdpUserBuilder.anIdpUser;

public class JDBIUserRepositoryTest {

    private Jdbi jdbi;
    private JDBIUserRepository repository;
    private UserMapper userMapper;
    // Unfortunately H2 does not support JSON type and we need to do things differently inmemory
    private boolean isPostgresDb = false;

    @Before
    public void setUp() {
        if (isPostgresDb) {
            jdbi = Jdbi.create("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=password");
        } else {
            jdbi = Jdbi.create("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
            createDb(jdbi);
        }

        ObjectMapper mapper = new ObjectMapper() {{
            registerModule(new JodaModule());
            registerModule(new GuavaModule());
        }};

        userMapper = new UserMapper(mapper);

        repository = new JDBIUserRepository(jdbi, userMapper);
    }

    // Unfortunately H2 does not support JSON type and this test can only work for PostgreSQL
    @Test
    @Ignore
    public void addOrUpdateUserForIdpShouldAddRecordIfUserDoesNotExist() {
        ensureNoUserExistsFor("some-idp-friendly-id");

        IdpUser idpUser = anIdpUser()
            .withUsername("some-username")
            .build();

        repository.addOrUpdateUserForIdp("some-idp-friendly-id", idpUser);

        List<IdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(1);
        assertThat(idpUsers.get(0)).isEqualTo(idpUser);
    }

    // Unfortunately H2 does not support JSON type and this test can only work for PostgreSQL
    @Test
    @Ignore
    public void addOrUpdateUserForIdpShouldUpdateRecordIfUserAlreadyExists() {
        IdpUser someUser = anIdpUser()
            .withUsername("some-username")
            .withPassword("some-password")
            .build();

        ensureNoUserExistsFor("some-idp-friendly-id");
        ensureUserExistsFor("some-idp-friendly-id", someUser);

        IdpUser sameUserDifferentPassword = anIdpUser()
            .withUsername("some-username")
            .withPassword("another-password")
            .build();

        repository.addOrUpdateUserForIdp("some-idp-friendly-id", sameUserDifferentPassword);

        List<IdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(1);
        assertThat(idpUsers.get(0)).isEqualTo(sameUserDifferentPassword);
    }

    @Test
    public void deleteUserFromIdpShouldDeleteGivenUserFromGivenIdp() {
        IdpUser someUser = anIdpUser()
            .withUsername("some-username")
            .build();

        ensureUserExistsFor("some-idp-friendly-id", someUser);

        repository.deleteUserFromIdp("some-idp-friendly-id", "some-username");

        List<IdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(0);
    }

    @Test
    public void getUsersForIdpShouldReturnAllUsersForGivenIdp() {
        ensureNoUserExistsFor("some-idp-friendly-id");

        IdpUser firstUser = anIdpUser().withUsername("first-username").build();
        IdpUser secondUser = anIdpUser().withUsername("second-username").build();

        ensureUserExistsFor("some-idp-friendly-id", firstUser);
        ensureUserExistsFor("some-idp-friendly-id", secondUser);

        List<IdpUser> idpUsers = new ArrayList<>(repository.getUsersForIdp("some-idp-friendly-id"));

        assertThat(idpUsers).size().isEqualTo(2);
    }

    private void ensureNoUserExistsFor(String idpFriendlyId) {
        jdbi.withHandle(handle ->
            handle.createUpdate("DELETE FROM users WHERE identity_provider_friendly_id = :idpFriendlyId")
                .bind("idpFriendlyId", idpFriendlyId)
                .execute()
        );
    }

    private void ensureUserExistsFor(String idpFriendlyId, IdpUser idpUser) {
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

    private void createDb(Jdbi jdbi) {
        String createDbScript =
            "DROP TABLE IF EXISTS USERS; \n" +
                "CREATE TABLE users (\n" +
                " id serial PRIMARY KEY,\n" +
                " username VARCHAR (50) UNIQUE NOT NULL,\n" +
                " password VARCHAR (50) NOT NULL,\n" +
                " identity_provider_friendly_id VARCHAR (255) NOT NULL,\n" +
                // NOTE: this is JSON in reality, but H2 doesn't support that.
                " \"data\" TEXT NOT NULL\n" +
                ")";

        jdbi.withHandle(handle ->
            handle.createScript(createDbScript).execute()
        );
    }
}