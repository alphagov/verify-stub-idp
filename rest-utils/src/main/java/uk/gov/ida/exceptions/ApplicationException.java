package uk.gov.ida.exceptions;

import com.google.common.base.Optional;
import uk.gov.ida.common.ErrorStatusDto;
import uk.gov.ida.common.ExceptionType;

import java.net.URI;
import java.text.MessageFormat;
import java.util.UUID;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static java.text.MessageFormat.format;

public final class ApplicationException extends RuntimeException {

    public static final String ERROR_MESSAGE_FORMAT = "{0}\nClient Message: {1}";
    private final UUID errorId;
    private final ExceptionType exceptionType;
    private final boolean audited;
    private final Optional<URI> uri;
    private final Optional<String> clientMessage;

    private ApplicationException(
            ExceptionType exceptionType,
            boolean audited,
            UUID errorId) {

        this(exceptionType, audited, errorId, null, Optional.<URI>absent(), Optional.<String>absent());
    }

    private ApplicationException(
            ExceptionType exceptionType,
            boolean audited,
            UUID errorId,
            Throwable cause) {

        this(exceptionType, audited, errorId, cause, Optional.<URI>absent(), Optional.<String>absent());
    }

    private ApplicationException(
            ExceptionType exceptionType,
            boolean audited,
            UUID errorId,
            Throwable cause,
            Optional<URI> uri,
            Optional<String> clientMessage) {

        super(format("Exception of type [{0}] {1}", exceptionType, getUriErrorMessage(uri)), cause);

        this.exceptionType = exceptionType;
        this.errorId = errorId;
        this.audited = audited;
        this.uri = uri;
        this.clientMessage = clientMessage;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (clientMessage.isPresent()) {
            return MessageFormat.format(ERROR_MESSAGE_FORMAT, message, clientMessage.get());
        } else {
            return message;
        }
    }

    private ApplicationException(ErrorStatusDto errorStatus, URI uri) {
        this(
                errorStatus.getExceptionType(),
                errorStatus.isAudited(),
                errorStatus.getErrorId(),
                null,
                fromNullable(uri),
                fromNullable(errorStatus.getClientMessage())
        );
    }

    public static ApplicationException createUnauditedException(ExceptionType exceptionType, UUID errorId) {
        return new ApplicationException(exceptionType, false, errorId);
    }

    public static ApplicationException createUnauditedException(ExceptionType exceptionType, String message, Throwable cause) {
        return new ApplicationException(exceptionType, false, UUID.randomUUID(), cause, Optional.<URI>absent(), Optional.of(message));
    }

    public static ApplicationException createUnauditedException(ExceptionType exceptionType, UUID errorId, Throwable cause, URI uri) {
        return new ApplicationException(exceptionType, false, errorId, cause, fromNullable(uri), Optional.<String>absent());
    }

    public static ApplicationException createUnauditedException(ExceptionType exceptionType, UUID errorId, Throwable cause) {
        return new ApplicationException(exceptionType, false, errorId, cause);
    }

    public static ApplicationException createAuditedException(ExceptionType exceptionType, UUID errorId) {
        return new ApplicationException(exceptionType, true, errorId);
    }

    public static ApplicationException createUnauditedException(ExceptionType exceptionType, UUID errorId, URI uri) {
        return new ApplicationException(exceptionType, false, errorId, null, fromNullable(uri), Optional.<String>absent());
    }

    public static ApplicationException createExceptionFromErrorStatusDto(ErrorStatusDto errorStatusDto, final URI uri) {
        return new ApplicationException(errorStatusDto, uri);
    }

    private static String getUriErrorMessage(Optional<URI> uri) {
        if (uri.isPresent()) {
            return format("whilst contacting uri: {0}", uri.get());
        }
        return "";
    }

    public UUID getErrorId() {
        return errorId;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public boolean isAudited() {
        return audited;
    }

    public Optional<URI> getUri() {
        return uri;
    }

    public boolean requiresAuditing() {
        return exceptionType != ExceptionType.NETWORK_ERROR;
    }
}
