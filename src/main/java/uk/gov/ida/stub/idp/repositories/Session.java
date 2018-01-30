package uk.gov.ida.stub.idp.repositories;

import uk.gov.ida.common.SessionId;
import uk.gov.ida.notification.saml.translation.EidasAuthnRequest;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;
import uk.gov.ida.stub.idp.domain.IdpUser;

import java.util.List;
import java.util.Optional;

public class Session {

    private final SessionId sessionId;
    private Optional<IdpUser> idpUser = Optional.empty();
    private final String relayState;
    private final List<IdpHint> validHints;
    private final List<String> invalidHints;
    private final Optional<IdpLanguageHint> languageHint;
    private final Optional<Boolean> registration;
    private final IdaAuthnRequestFromHub idaAuthnRequestFromHub;
    private final EidasAuthnRequest eidasAuthnRequest;

    public Session(SessionId sessionId, IdaAuthnRequestFromHub idaAuthnRequestFromHub, String relayState, List<IdpHint> validHints, List<String> invalidHints, Optional<IdpLanguageHint> languageHint, Optional<Boolean> registration) {
        this.sessionId = sessionId;
        this.idaAuthnRequestFromHub = idaAuthnRequestFromHub;
        this.eidasAuthnRequest = null;
        this.relayState = relayState;
        this.validHints = validHints;
        this.invalidHints = invalidHints;
        this.languageHint = languageHint;
        this.registration = registration;
    }

    public Session(SessionId sessionId, EidasAuthnRequest eidasAuthnRequest, String relayState, List<IdpHint> validHints, List<String> invalidHints, Optional<IdpLanguageHint> languageHint, Optional<Boolean> registration) {
        this.sessionId = sessionId;
        this.eidasAuthnRequest = eidasAuthnRequest;
        this.idaAuthnRequestFromHub = null;
        this.relayState = relayState;
        this.validHints = validHints;
        this.invalidHints = invalidHints;
        this.languageHint = languageHint;
        this.registration = registration;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public void setIdpUser(Optional<IdpUser> idpUser) {
        this.idpUser = idpUser;
    }

    public Optional<IdpUser> getIdpUser() {
        return idpUser;
    }

    public String getRelayState() {
        return relayState;
    }

    public IdaAuthnRequestFromHub getIdaAuthnRequestFromHub() {
        return idaAuthnRequestFromHub;
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

    public EidasAuthnRequest getEidasAuthnRequest() {
        return eidasAuthnRequest;
    }
}
