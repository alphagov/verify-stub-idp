package uk.gov.ida.saml.core.domain;

public enum TransactionIdaStatus implements IdaStatus {
    Success,
    RequesterError,
    NoAuthenticationContext,
    NoMatchingServiceMatchFromHub,
    AuthenticationFailed
}
