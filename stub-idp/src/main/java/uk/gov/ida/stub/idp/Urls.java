package uk.gov.ida.stub.idp;

public interface Urls {
    // params
    String SAML_REQUEST_PARAM = "SAMLRequest";
    String HINTS_PARAM = "hint";
    String RELAY_STATE_PARAM = "RelayState";
    String LANGUAGE_HINT_PARAM = "language";
    String IDP_ID_PARAM = "idpId";
    String SCHEME_ID_PARAM = "schemeId";
    String REGISTRATION_PARAM = "registration";
    String SUBMIT_PARAM = "submit";
    String ERROR_MESSAGE_PARAM = "errorMessage";
    String SINGLE_IDP_JOURNEY_ID_PARAM = "singleIdpJourneyIdentifier";
    @SuppressWarnings("squid:S2068")
    String PASSWORD_PARAM = "password";
    String USERNAME_PARAM = "username";
    String LOGIN_FAILURE_STATUS_PARAM = "failureStatus";
    String REQUESTER_ERROR_MESSAGE_PARAM = "requesterErrorMessage";
    String DATE_OF_BIRTH_PARAM = "dateOfBirth";
    String INCLUDE_GENDER_PARAM = "includeGender";
    String GENDER_PARAM = "gender";
    String ADDRESS_POST_CODE_PARAM = "addressPostCode";
    String ADDRESS_TOWN_PARAM = "addressTown";
    String ADDRESS_LINE2_PARAM = "addressLine2";
    String ADDRESS_LINE1_PARAM = "addressLine1";
    String SURNAME_PARAM = "surname";
    String NON_LATIN_SURNAME_PARAM = "nonLatinSurname";
    String FIRSTNAME_PARAM = "firstname";
    String NON_LATIN_FIRSTNAME_PARAM = "nonLatinFirstname";
    String KNOWN_HINTS_PARAM = "known_hint";
    String UNKNOWN_HINTS_PARAM = "unknown_hint";
    String RANDOMISE_PID_PARAM = "randomPid";
    String CYCLE3_PARAM = "c3";
    String LEVEL_OF_ASSURANCE_PARAM = "loa";
    String SIGNING_ALGORITHM_PARAM = "signingAlgorithm";
    String SOURCE_PARAM = "source";
    String SOURCE_PARAM_PRE_REG_VALUE = "pre-reg";

    // comedy routes
    String ROOT_PREFIX = "/~willp-bl/cgi-bin"; //can be left blank, needs no trailing slash if not blank
    String ROUTE_SUFFIX = ".php"; // can be left blank

    // paths and resources
    String IDP_SAML2_SSO_RESOURCE = "/{"+IDP_ID_PARAM+"}/SAML2/SSO";
    String EIDAS_SAML2_SSO_RESOURCE = "/eidas/{"+SCHEME_ID_PARAM+"}/SAML2/SSO";
    String HEADLESS_ROOT = "/headless";

    String LOGIN_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/login" + ROUTE_SUFFIX;
    String EIDAS_LOGIN_RESOURCE = ROOT_PREFIX + "/eidas/{"+SCHEME_ID_PARAM+"}/login" + ROUTE_SUFFIX;
    String LOGIN_AUTHN_FAILURE_PATH = "authn-failure";
    String LOGIN_NO_AUTHN_CONTEXT_PATH = "no-authn-context";
    String LOGIN_FRAUD_FAILURE_PATH = "fraud-failure";
    String LOGIN_UPLIFT_FAILED_PATH = "uplift-failed";
    String LOGIN_REQUESTER_ERROR_PATH = "requester-error";
    String LOGOUT_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/logout" + ROUTE_SUFFIX;
    String HOMEPAGE_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}";

    String REGISTER_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/register" + ROUTE_SUFFIX;
    String PRE_REGISTER_PATH = "pre-register";
    String PRE_REGISTER_RESOURCE = REGISTER_RESOURCE + "/" + PRE_REGISTER_PATH;
    String CANCEL_PRE_REGISTER_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/cancel-pre-register" + ROUTE_SUFFIX;
    String LOGIN_AUTHN_PENDING_PATH = "authn-pending";
    String DEBUG_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/debug" + ROUTE_SUFFIX;
    String CONSENT_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/consent" + ROUTE_SUFFIX;
    String SINGLE_IDP_PROMPT_RESOURCE = ROOT_PREFIX + "/{"+IDP_ID_PARAM+"}/start-prompt" + ROUTE_SUFFIX;

    String EIDAS_REGISTER_RESOURCE = ROOT_PREFIX + "/eidas/{"+SCHEME_ID_PARAM+"}/register" + ROUTE_SUFFIX;
    String EIDAS_CONSENT_RESOURCE = ROOT_PREFIX + "/eidas/{"+SCHEME_ID_PARAM+"}/consent" + ROUTE_SUFFIX;
    String METADATA_RESOURCE = "/{"+SCHEME_ID_PARAM+"}/ServiceMetadata";
    String EIDAS_DEBUG_RESOURCE = ROOT_PREFIX + "/eidas/{"+SCHEME_ID_PARAM+"}/debug" + ROUTE_SUFFIX;

    String USERS_RESOURCE = "/{"+IDP_ID_PARAM+"}/users";
    String DELETE_USER_PATH = "/delete";
    String GET_USER_PATH = "/{" + USERNAME_PARAM + "}";

    @SuppressWarnings("squid:S2068")
    String PASSWORD_GEN_RESOURCE = "/password-gen";

}
