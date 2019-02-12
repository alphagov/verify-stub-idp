package uk.gov.ida.saml.hub.domain;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.IdaSamlMessage;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public class IdaAuthnRequestFromHub extends IdaSamlMessage {
    private List<AuthnContext> levelsOfAssurance;
    private Optional<Boolean> forceAuthentication;
    private DateTime sessionExpiryTimestamp;
    private final AuthnContextComparisonTypeEnumeration comparisonType;

    public IdaAuthnRequestFromHub(
            String id,
            String issuer,
            DateTime issueInstant,
            List<AuthnContext> levelsOfAssurance,
            Optional<Boolean> forceAuthentication,
            DateTime sessionExpiryTimestamp,
            URI idpPostEndpoint,
            AuthnContextComparisonTypeEnumeration comparisonType) {
        super(id, issuer, issueInstant, idpPostEndpoint);
        this.levelsOfAssurance = levelsOfAssurance;
        this.forceAuthentication = forceAuthentication;
        this.sessionExpiryTimestamp = sessionExpiryTimestamp;
        this.comparisonType = comparisonType;
    }

    public static IdaAuthnRequestFromHub createRequestToSendFromHub(
            String id,
            List<AuthnContext> levelsOfAssurance,
            Optional<Boolean> forceAuthentication,
            DateTime sessionExpiryTimestamp,
            URI idpPostEndpoint,
            AuthnContextComparisonTypeEnumeration comparisonType,
            String hubEntityId) {
        return new IdaAuthnRequestFromHub(id, hubEntityId, DateTime.now(), levelsOfAssurance, forceAuthentication, sessionExpiryTimestamp, idpPostEndpoint, comparisonType);
    }

    public static IdaAuthnRequestFromHub createRequestReceivedFromHub(String id, String issuerId, List<AuthnContext> levelsOfAssurance, boolean forceAuthentication, DateTime notOnOrAfter, AuthnContextComparisonTypeEnumeration comparisonType) {
        return new IdaAuthnRequestFromHub(id, issuerId, DateTime.now(), levelsOfAssurance, Optional.ofNullable(forceAuthentication), notOnOrAfter, null, comparisonType); // null because it was sent to the consumer of this message
    }

    public Optional<Boolean> getForceAuthentication() {
        return forceAuthentication;
    }

    public DateTime getSessionExpiryTimestamp() {
        return sessionExpiryTimestamp;
    }

    public List<AuthnContext> getLevelsOfAssurance() {
        return levelsOfAssurance;
    }

    public AuthnContextComparisonTypeEnumeration getComparisonType() {
        return comparisonType;
    }
}
