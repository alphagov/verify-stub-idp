package uk.gov.ida.stub.idp.healthcheck;


import com.codahale.metrics.health.HealthCheck;

public class StubIdpHealthCheck extends HealthCheck {
    public StubIdpHealthCheck() {
        super();
    }

    public String getName() {
        return "Stub idp Health Check";
    }

    @Override
    protected Result check() {
        return Result.healthy();
    }
}
