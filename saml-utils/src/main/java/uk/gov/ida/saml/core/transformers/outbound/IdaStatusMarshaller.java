package uk.gov.ida.saml.core.transformers.outbound;

import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusDetail;
import org.opensaml.saml.saml2.core.StatusMessage;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.DetailedStatusCode;
import uk.gov.ida.saml.core.domain.IdaStatus;

import java.text.MessageFormat;
import java.util.Optional;

import static java.util.Optional.empty;

public abstract class IdaStatusMarshaller<T extends IdaStatus> {

    protected final OpenSamlXmlObjectFactory samlObjectFactory;

    public IdaStatusMarshaller(OpenSamlXmlObjectFactory samlObjectFactory) {
        this.samlObjectFactory = samlObjectFactory;
    }

    public Status toSamlStatus(T originalStatus) {
        DetailedStatusCode detailedStatusCode = getDetailedStatusCode(originalStatus);

        if (detailedStatusCode == null)
            throw new UnsupportedOperationException(MessageFormat.format("Unrecognised status: {0}", originalStatus));

        return createStatus(detailedStatusCode, getStatusMessage(originalStatus), getStatusDetail(originalStatus));
    }

    protected Optional<String> getStatusMessage(T originalStatus) {
        return empty();
    }
    protected abstract DetailedStatusCode getDetailedStatusCode(T originalStatus);

    protected Optional<StatusDetail> getStatusDetail(T originalStatus) {
        return empty();
    }

    private Status createStatus(DetailedStatusCode detailedStatusCode, Optional<String> message, Optional<StatusDetail> statusDetail) {
        Status transformedStatus = samlObjectFactory.createStatus();
        if (message.isPresent()) {
            StatusMessage statusMessage = samlObjectFactory.createStatusMessage();
            statusMessage.setMessage(message.get());
            transformedStatus.setStatusMessage(statusMessage);
        }

        StatusCode topLevelStatusCode = samlObjectFactory.createStatusCode();
        topLevelStatusCode.setValue(detailedStatusCode.getStatus());
        if (detailedStatusCode.getSubStatus().isPresent()) {
            StatusCode subStatusCode = samlObjectFactory.createStatusCode();
            subStatusCode.setValue(detailedStatusCode.getSubStatus().get());
            topLevelStatusCode.setStatusCode(subStatusCode);
        }
        if (statusDetail.isPresent()) {
            transformedStatus.setStatusDetail(statusDetail.get());
        }
        transformedStatus.setStatusCode(topLevelStatusCode);
        return transformedStatus;
    }

}
