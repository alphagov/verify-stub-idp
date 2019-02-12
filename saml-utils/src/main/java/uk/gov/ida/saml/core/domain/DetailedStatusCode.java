package uk.gov.ida.saml.core.domain;

import org.opensaml.saml.saml2.core.StatusCode;

import java.util.Optional;

public enum DetailedStatusCode {
    Success(StatusCode.SUCCESS),
    MatchingServiceMatch(StatusCode.SUCCESS, SamlStatusCode.MATCH),
    NoAuthenticationContext(StatusCode.RESPONDER, StatusCode.NO_AUTHN_CONTEXT),
    SuccessNoAuthenticationContext(StatusCode.SUCCESS, StatusCode.NO_AUTHN_CONTEXT),
    NoMatchingServiceMatchFromHub(StatusCode.SUCCESS, SamlStatusCode.NO_MATCH),
    SamlProfileNoMatchingServiceMatchFromHub(StatusCode.RESPONDER, SamlStatusCode.NO_MATCH),
    SimpleProfileNoMatchingServiceMatchFromHub(StatusCode.RESPONDER, SamlStatusCode.NO_MATCH),
    NoMatchingServiceMatchFromMatchingService(StatusCode.RESPONDER, SamlStatusCode.NO_MATCH),
    AuthenticationFailed(StatusCode.RESPONDER, StatusCode.AUTHN_FAILED),
    RequesterErrorFromIdp(StatusCode.REQUESTER),
    RequesterErrorRequestDeniedFromIdp(StatusCode.REQUESTER,StatusCode.REQUEST_DENIED),
    RequesterErrorFromIdpAsSentByHub(StatusCode.RESPONDER, StatusCode.REQUESTER),
    Healthy(StatusCode.SUCCESS, SamlStatusCode.HEALTHY),
    UnknownUserCreateFailure(StatusCode.RESPONDER, SamlStatusCode.CREATE_FAILURE),
    UnknownUserNoAttributeFailure(StatusCode.RESPONDER),
    UnknownUserCreateSuccess(StatusCode.SUCCESS, SamlStatusCode.CREATED);

    private final String status;
    private final Optional<String> subStatus;

    DetailedStatusCode(final String status) {
        this.status = status;
        this.subStatus = Optional.empty();
    }

    DetailedStatusCode(final String status, final String subStatus) {
        this.status = status;
        this.subStatus = Optional.ofNullable(subStatus);
    }

    public String getStatus() {
        return status;
    }

    public Optional<String> getSubStatus() {
        return subStatus;
    }
}
