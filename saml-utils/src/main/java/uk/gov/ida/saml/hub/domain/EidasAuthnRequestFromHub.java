package uk.gov.ida.saml.hub.domain;

import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.IdaSamlMessage;

import java.net.URI;
import java.util.List;

public class EidasAuthnRequestFromHub extends IdaSamlMessage {
    private final List<AuthnContext> levelsOfAssurance;
    private final String providerName;

    public EidasAuthnRequestFromHub(
        String id,
        String issuer,
        DateTime issueInstant,
        List<AuthnContext> levelsOfAssurance,
        URI countryPostEndpoint,
        String providerName) {
        super(id, issuer, issueInstant, countryPostEndpoint);
        this.levelsOfAssurance = levelsOfAssurance;
        this.providerName = providerName;
    }

    public static EidasAuthnRequestFromHub createRequestToSendFromHub(String id, List<AuthnContext> levelsOfAssurance, URI countryPostEndpoint, String providerName, String hubEntityId) {
        return new EidasAuthnRequestFromHub(id, hubEntityId, DateTime.now(), levelsOfAssurance, countryPostEndpoint, providerName);
    }


    public String getProviderName() {
        return providerName;
    }

    public List<AuthnContext> getLevelsOfAssurance() {
        return levelsOfAssurance;
    }
}
