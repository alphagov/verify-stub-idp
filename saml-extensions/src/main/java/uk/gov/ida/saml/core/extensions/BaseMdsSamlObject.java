package uk.gov.ida.saml.core.extensions;

public interface BaseMdsSamlObject {

    String FROM_ATTRIB_NAME = "From";
    String TO_ATTRIB_NAME = "To";
    String VERIFIED_ATTRIB_NAME = "Verified";

    org.joda.time.DateTime getFrom();

    void setFrom(org.joda.time.DateTime fromTime);

    org.joda.time.DateTime getTo();

    void setTo(org.joda.time.DateTime toTime);

    boolean getVerified();

    void setVerified(boolean verified);
}
