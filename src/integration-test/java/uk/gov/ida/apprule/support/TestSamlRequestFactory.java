package uk.gov.ida.apprule.support;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.AuthnRequest;
import uk.gov.ida.saml.core.test.TestCertificateStrings;
import uk.gov.ida.saml.core.test.TestCredentialFactory;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.core.test.builders.IssuerBuilder;
import uk.gov.ida.saml.hub.domain.Endpoints;
import uk.gov.ida.saml.serializers.XmlObjectToBase64EncodedStringTransformer;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PRIVATE_SIGNING_KEY;
import static uk.gov.ida.saml.core.test.TestCertificateStrings.HUB_TEST_PUBLIC_SIGNING_CERT;

public class TestSamlRequestFactory {

    private static XmlObjectToBase64EncodedStringTransformer<AuthnRequest> toBase64EncodedStringTransformer = new XmlObjectToBase64EncodedStringTransformer<>();

    public static String anAuthnRequest() {
        return anAuthRequestForId(UUID.randomUUID().toString());
    }

    public static String anAuthRequestForId(String requestId) {
        return anAuthnRequestX(requestId,
                TestEntityIds.HUB_ENTITY_ID,
                Optional.ofNullable(false),
                Optional.ofNullable(0),
                HUB_TEST_PUBLIC_SIGNING_CERT,
                HUB_TEST_PRIVATE_SIGNING_KEY,
                Endpoints.SSO_REQUEST_ENDPOINT,
                Optional.ofNullable(DateTime.now()));
    }

    private static String anAuthnRequestX(
            String id,
            String issuer,
            Optional<Boolean> forceAuthentication,
            Optional<Integer> assertionConsumerServiceIndex,
            String publicCert,
            String privateKey,
            String ssoRequestEndpoint,
            Optional<DateTime> issueInstant) {
        AuthnRequest authnRequest = getAuthnRequest(id, issuer, forceAuthentication, assertionConsumerServiceIndex, publicCert, privateKey, ssoRequestEndpoint, issueInstant);
        return toBase64EncodedStringTransformer.apply(authnRequest);
    }

    private static AuthnRequest getAuthnRequest(
            String id,
            String issuer,
            Optional<Boolean> forceAuthentication,
            Optional<Integer> assertionConsumerServiceIndex,
            String publicCert,
            String privateKey,
            String ssoRequestEndpoint,
            Optional<DateTime> issueInstant) {
        AuthnRequestBuilder authnRequestBuilder = AuthnRequestBuilder.anAuthnRequest()
                .withId(id)
                .withIssuer(IssuerBuilder.anIssuer().withIssuerId(issuer).build())
                .withDestination("http://localhost" + ssoRequestEndpoint)
                .withSigningCredential(new TestCredentialFactory(publicCert, privateKey).getSigningCredential());

        forceAuthentication.ifPresent(authnRequestBuilder::withForceAuthn);
        assertionConsumerServiceIndex.ifPresent(authnRequestBuilder::withAssertionConsumerServiceIndex);
        issueInstant.ifPresent(authnRequestBuilder::withIssueInstant);

        return authnRequestBuilder.build();
    }

}
