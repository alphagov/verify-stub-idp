package uk.gov.ida.stub.idp.bundles;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import uk.gov.ida.stub.idp.configuration.StubIdpConfiguration;
import uk.gov.ida.stub.idp.repositories.jdbc.migrations.DatabaseMigrationRunner;

public class DatabaseMigrationBundle implements ConfiguredBundle<StubIdpConfiguration> {
    @Override
    public void run(StubIdpConfiguration configuration, Environment environment) {
        new DatabaseMigrationRunner().runMigration(configuration.getDatabaseConfiguration().getUrl());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
