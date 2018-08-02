package uk.gov.ida.stub.idp.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.jdbi.v3.core.Jdbi;

import static com.codahale.metrics.health.HealthCheck.Result.healthy;
import static com.codahale.metrics.health.HealthCheck.Result.unhealthy;

public class DatabaseHealthCheck extends HealthCheck {

    private final String dbUrl;

    public DatabaseHealthCheck(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    @Override
    protected Result check() {
        try {
            Jdbi jdbi = Jdbi.create(dbUrl);

            jdbi.withHandle(handle ->
                handle.createQuery("select 1 from users")
                    .mapTo(Integer.class)
                    .findFirst()
                    .isPresent()

            );

            return healthy();
        } catch (Exception e) {
            return unhealthy("Error while connecting to the Database: " + e.getMessage());
        }
    }
}

