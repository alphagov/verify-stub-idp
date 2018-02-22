package uk.gov.ida.stub.idp.repositories.jdbc.migrations;

import org.jdbi.v3.core.Jdbi;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseMigrationRunnerTest {

    private static final String DATABASE_URL = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

    private DatabaseMigrationRunner migrationRunner = new DatabaseMigrationRunner();

    @Test
    public void shouldRunMigrationScripts() {
        migrationRunner.runMigration(DATABASE_URL);

        assertThat(dbHasRecords(DATABASE_URL)).isTrue();
    }

    private Boolean dbHasRecords(String dbUrl) {
        return Jdbi.create(dbUrl).withHandle(handle ->
            handle.createQuery("select 1 from users")
                .mapTo(Integer.class)
                .findFirst()
                .get()
                .equals(1)
        );
    }
}