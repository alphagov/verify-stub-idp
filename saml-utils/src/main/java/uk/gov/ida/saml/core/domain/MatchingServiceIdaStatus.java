package uk.gov.ida.saml.core.domain;

public enum MatchingServiceIdaStatus implements IdaStatus {
    NoMatchingServiceMatchFromMatchingService,
    RequesterError,
    MatchingServiceMatch,
    UserAccountCreated,
    Healthy
    }
