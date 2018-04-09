package uk.gov.ida.stub.idp.domain.factories;

import io.dropwizard.util.Duration;
import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.stub.idp.configuration.AssertionLifetimeConfiguration;

import javax.inject.Inject;
import javax.inject.Named;

public class AssertionRestrictionsFactory {
    private final Duration assertionLifetime;
    private final String hubEntityId;

    @Inject
    public AssertionRestrictionsFactory(AssertionLifetimeConfiguration assertionTimeoutConfig,
                                        @Named("HubEntityId") String hubEntityId) {
        assertionLifetime = assertionTimeoutConfig.getAssertionLifetime();
        this.hubEntityId = hubEntityId;
    }

    public AssertionRestrictions createRestrictionsForSendingToHub(String inResponseTo) {
        return new AssertionRestrictions(DateTime.now().plus(assertionLifetime.toMilliseconds()), inResponseTo, hubEntityId);
    }

    public AssertionRestrictions create(String inResponseTo, String recipient) {
        return new AssertionRestrictions(DateTime.now().plus(assertionLifetime.toMilliseconds()), inResponseTo, recipient);
    }
}
