package uk.gov.ida.saml.core.test.builders;

import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

public class StatusBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<StatusCode> statusCode = Optional.of(StatusCodeBuilder.aStatusCode().build());
    private Optional<StatusMessage> message = Optional.empty();


    public static StatusBuilder aStatus() {
        return new StatusBuilder();
    }

    public Status build() {
        Status status = openSamlXmlObjectFactory.createStatus();

        if (statusCode.isPresent()) {
            status.setStatusCode(statusCode.get());
        }

        if (message.isPresent()) {
            status.setStatusMessage(message.get());
        }

        return status;
    }

    public StatusBuilder withStatusCode(StatusCode statusCode) {
        this.statusCode = Optional.ofNullable(statusCode);
        return this;
    }

    public StatusBuilder withMessage(StatusMessage message) {
        this.message = Optional.ofNullable(message);
        return this;
    }
}
