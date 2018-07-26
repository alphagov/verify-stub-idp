package uk.gov.ida.stub.idp.repositories.jdbc.migrations;

import org.flywaydb.core.Flyway;

public class DatabaseMigrationRunner {

    public void runMigration(String dbUrl) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dbUrl, null, null);
        flyway.setBaselineVersionAsString("0");
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("classpath:db.migrations.common", getDBSpecificMigration(dbUrl));

        flyway.migrate();
    }

    private String getDBSpecificMigration(String dbUrl) {
        return dbUrl.contains("h2") ? "classpath:db.migrations.h2" : "classpath:db.migrations.postgres";
    }
}
