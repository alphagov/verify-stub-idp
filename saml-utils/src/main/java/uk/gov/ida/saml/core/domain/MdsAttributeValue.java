package uk.gov.ida.saml.core.domain;

import org.joda.time.DateTime;

import java.util.Optional;

public interface MdsAttributeValue {
    DateTime getFrom();

    Optional<DateTime> getTo();

    boolean isVerified();
}
