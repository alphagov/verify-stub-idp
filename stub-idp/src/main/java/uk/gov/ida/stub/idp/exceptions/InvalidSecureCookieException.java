package uk.gov.ida.stub.idp.exceptions;

import uk.gov.ida.common.SessionId;

import java.util.Optional;

public class InvalidSecureCookieException extends RuntimeException {
    private SessionId sessionId;
    private final String message;

    public InvalidSecureCookieException(String message, SessionId sessionId) {
        super(message);
        this.message = message;
        this.sessionId = sessionId;
    }

    public Optional<SessionId> getSessionId() {
        return Optional.ofNullable(sessionId);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
