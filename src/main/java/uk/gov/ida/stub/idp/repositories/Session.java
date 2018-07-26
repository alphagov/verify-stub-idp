package uk.gov.ida.stub.idp.repositories;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;

import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Session {

    private final SessionId sessionId;
    private final String relayState;
    private final List<IdpHint> validHints;
    private final List<String> invalidHints;
    private final Optional<IdpLanguageHint> languageHint;
    private final Optional<Boolean> registration;
    
    public Session(@JsonProperty("sessionId") SessionId sessionId, @JsonProperty("relayState") String relayState,
                   @JsonProperty("validHints") List<IdpHint> validHints, @JsonProperty("invalidHints") List<String> invalidHints, @JsonProperty("languageHint") Optional<IdpLanguageHint> languageHint, @JsonProperty("registration") Optional<Boolean> registration) {
        this.sessionId = sessionId;
        this.relayState = relayState;
        this.validHints = validHints;
        this.invalidHints = invalidHints;
        this.languageHint = languageHint;
        this.registration = registration;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public String getRelayState() {
        return relayState;
    }

    public List<IdpHint> getValidHints() {
        return validHints;
    }

    public List<String> getInvalidHints() {
        return invalidHints;
    }

    public Optional<IdpLanguageHint> getLanguageHint() {
        return languageHint;
    }

    public Optional<Boolean> isRegistration() {
        return registration;
    }
}
