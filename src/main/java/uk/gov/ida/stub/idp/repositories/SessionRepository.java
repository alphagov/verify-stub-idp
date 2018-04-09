package uk.gov.ida.stub.idp.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import uk.gov.ida.common.SessionId;
import uk.gov.ida.saml.hub.domain.IdaAuthnRequestFromHub;
import uk.gov.ida.stub.idp.domain.IdpHint;
import uk.gov.ida.stub.idp.domain.IdpLanguageHint;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SessionRepository {

    private Cache<SessionId, Session> sessions;

    @Inject
    public SessionRepository(@Named("sessionCacheTimeoutInMinutes") Integer sessionCacheTimeoutInMinutes) {
        this.sessions = CacheBuilder.newBuilder()
                .expireAfterWrite(sessionCacheTimeoutInMinutes, TimeUnit.MINUTES)
                .build();
    }

    public Optional<Session> get(SessionId sessionToken) {
        if (!sessions.asMap().containsKey(sessionToken)) {
            return Optional.empty();
        }
        return Optional.ofNullable(sessions.asMap().get(sessionToken));
    }

    public SessionId newSession(IdaAuthnRequestFromHub idaRequestFromHub, String relayState, List<IdpHint> validHints, List<String> invalidHints, Optional<IdpLanguageHint> languageHint, Optional<Boolean> registration) {
        SessionId idpSessionId = SessionId.createNewSessionId();
        Session session = new Session(idpSessionId, idaRequestFromHub, relayState, validHints, invalidHints, languageHint, registration);
        return updateSession(idpSessionId, session);
    }

    public SessionId updateSession(SessionId id, Session session) {
        sessions.put(id, session);
        return id;
    }

    public Optional<Session> deleteAndGet(SessionId sessionToken) {
        return Optional.ofNullable(sessions.asMap().remove(sessionToken));

    }

}
