package uk.gov.ida.saml.core.validation;

public class TransformationErrorContext {
    private final String messageId;
    private final String entityId;
    private final String messageType;

    public TransformationErrorContext(String messageId, String entityId, String messageType) {
        this.messageId = messageId;
        this.entityId = entityId;
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "{" +
                "messageId='" + messageId + '\'' +
                ", entityId='" + entityId + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }

    public String getEntityId() {
        return entityId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }
}
