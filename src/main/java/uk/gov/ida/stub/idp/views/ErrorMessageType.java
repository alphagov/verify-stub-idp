package uk.gov.ida.stub.idp.views;

public enum ErrorMessageType {
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password."),
    INVALID_SESSION_ID("Invalid session id."),
    USERNAME_ALREADY_TAKEN("That username is already taken - please choose another."),
    INVALID_DATE("Please enter a date in the format 'yyyy-mm-dd'."),
    INCOMPLETE_REGISTRATION("Incomplete registration, please enter all required information."),
    NO_ERROR("");

    private final String message;

    ErrorMessageType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
