package uk.gov.ida.common;

import java.util.UUID;

public class ErrorStatusDto {

    private boolean audited;
    private UUID errorId;
    private ExceptionType exceptionType;
    private String clientMessage;

    protected ErrorStatusDto() {}

    private ErrorStatusDto(UUID errorId, ExceptionType exceptionType, boolean audited, String clientMessage) {
        this.errorId = errorId;
        this.exceptionType = exceptionType;
        this.audited = audited;
        this.clientMessage = clientMessage;
    }

    public static ErrorStatusDto createUnauditedErrorStatus(UUID errorId, ExceptionType exceptionType) {
        return new ErrorStatusDto(errorId, exceptionType, false, "");
    }

    public static ErrorStatusDto createUnauditedErrorStatus(UUID errorId, ExceptionType exceptionType, String clientMessage) {
        return new ErrorStatusDto(errorId, exceptionType, false, clientMessage);
    }

    public static ErrorStatusDto createAuditedErrorStatus(UUID errorId, ExceptionType exceptionType) {
        return new ErrorStatusDto(errorId, exceptionType, true, "");
    }

    public static ErrorStatusDto createAuditedErrorStatus(UUID errorId, ExceptionType exceptionType, String clientMessage) {
        return new ErrorStatusDto(errorId, exceptionType, true, clientMessage);
    }

    public boolean isAudited() {
        return this.audited;
    }

    public UUID getErrorId() {
        return errorId;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public String getClientMessage() {
        return clientMessage;
    }
}
