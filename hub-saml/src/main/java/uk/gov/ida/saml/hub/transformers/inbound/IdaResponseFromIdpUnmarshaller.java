package uk.gov.ida.saml.hub.transformers.inbound;

import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.PassthroughAssertion;
import uk.gov.ida.saml.hub.domain.IdpIdaStatus;
import uk.gov.ida.saml.hub.domain.InboundResponseFromIdp;
import uk.gov.ida.saml.security.validators.ValidatedAssertions;
import uk.gov.ida.saml.security.validators.ValidatedResponse;

import java.net.URI;
import java.util.Optional;

public class IdaResponseFromIdpUnmarshaller {
    private final IdpIdaStatusUnmarshaller statusUnmarshaller;
    private final PassthroughAssertionUnmarshaller passthroughAssertionUnmarshaller;

    public IdaResponseFromIdpUnmarshaller(
            IdpIdaStatusUnmarshaller statusUnmarshaller,
            PassthroughAssertionUnmarshaller passthroughAssertionUnmarshaller) {
        this.statusUnmarshaller = statusUnmarshaller;
        this.passthroughAssertionUnmarshaller = passthroughAssertionUnmarshaller;
    }

    public InboundResponseFromIdp fromSaml(ValidatedResponse validatedResponse, ValidatedAssertions validatedAssertions) {
        Optional<PassthroughAssertion> matchingDatasetAssertion = validatedAssertions.getMatchingDatasetAssertion()
                .map(passthroughAssertionUnmarshaller::fromAssertion);

        Optional<PassthroughAssertion> authnStatementAssertion = validatedAssertions.getAuthnStatementAssertion()
                .map(passthroughAssertionUnmarshaller::fromAssertion);

        IdpIdaStatus transformedStatus = statusUnmarshaller.fromSaml(validatedResponse.getStatus());
        URI destination = URI.create(validatedResponse.getDestination());
        Optional<DateTime> notOnOrAfter = validatedAssertions.getMatchingDatasetAssertion()
                .flatMap(a -> Optional.ofNullable(a.getSubject()))
                .flatMap(s -> Optional.ofNullable(s.getSubjectConfirmations().get(0).getSubjectConfirmationData().getNotOnOrAfter()));

        return new InboundResponseFromIdp(
                validatedResponse.getID(),
                validatedResponse.getInResponseTo(),
                validatedResponse.getIssuer().getValue(),
                validatedResponse.getIssueInstant(),
                notOnOrAfter,
                transformedStatus,
                Optional.ofNullable(validatedResponse.getSignature()),
                matchingDatasetAssertion,
                destination,
                authnStatementAssertion);
    }

}
