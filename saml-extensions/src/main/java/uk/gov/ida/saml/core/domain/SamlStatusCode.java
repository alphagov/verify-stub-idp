package uk.gov.ida.saml.core.domain;

public abstract class SamlStatusCode {
    public static final String MATCH = "urn:uk:gov:cabinet-office:tc:saml:statuscode:match";
    public static final String NO_MATCH = "urn:uk:gov:cabinet-office:tc:saml:statuscode:no-match";
    public static final String MULTI_MATCH = "urn:uk:gov:cabinet-office:tc:saml:statuscode:multiple-match";
    public static final String HEALTHY = "urn:uk:gov:cabinet-office:tc:saml:statuscode:healthy";
    public static final String CREATE_FAILURE = "urn:uk:gov:cabinet-office:tc:saml:statuscode:create-failure";
    public static final String CREATED = "urn:uk:gov:cabinet-office:tc:saml:statuscode:created";
}
