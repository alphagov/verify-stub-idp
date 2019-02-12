package uk.gov.ida.saml.security.saml.builders;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class ResponseBuilder {

    private static TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();

    public static final String DEFAULT_REQUEST_ID = "default-request-id";
    public static final String DEFAULT_RESPONSE_ID = "default-response-id";

    private EncryptedAssertion defaultEncryptedAssertion;
    private boolean addDefaultEncryptedAssertionIfNoneIsAdded = true;
    private boolean shouldSign = true;
    private boolean shouldAddSignature = true;
    private SignatureAlgorithm signatureAlgorithm = new SignatureRSASHA256();
    private DigestAlgorithm digestAlgorithm = new DigestSHA256();
    private List<Assertion> assertions = new ArrayList<>();
    private List<EncryptedAssertion> encryptedAssertions = new ArrayList<>();

    private Optional<Issuer> issuer = ofNullable(IssuerBuilder.anIssuer().build());
    private Optional<String> id = ofNullable(DEFAULT_RESPONSE_ID);
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());
    private Optional<String> inResponseTo = ofNullable(DEFAULT_REQUEST_ID);
    private Optional<Status> status = ofNullable(getStatus());
    private Optional<Credential> signingCredential = empty();
    private Optional<String > destination = ofNullable("http://destination.com");

    public static ResponseBuilder aResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.defaultEncryptedAssertion = EncryptedAssertionBuilder.anEncryptedAssertionBuilder().build();
        return responseBuilder;
    }

    public Response build() throws MarshallingException, SignatureException {

        Response response = testSamlObjectFactory.createResponse();
        id.ifPresent(response::setID);
        inResponseTo.ifPresent(response::setInResponseTo);
        issueInstant.ifPresent(response::setIssueInstant);
        status.ifPresent(response::setStatus);

        destination.ifPresent(response::setDestination);

        response.getAssertions().addAll(assertions);
        if (encryptedAssertions.isEmpty() && addDefaultEncryptedAssertionIfNoneIsAdded) {
            response.getEncryptedAssertions().add(defaultEncryptedAssertion);
        } else {
            response.getEncryptedAssertions().addAll(encryptedAssertions);
        }

        if (issuer.isPresent()) {
            response.setIssuer(issuer.get());

            if (!Strings.isNullOrEmpty(issuer.get().getValue()) && shouldAddSignature) {
                SignatureBuilder signatureBuilder = SignatureBuilder.aSignature().withSignatureAlgorithm(signatureAlgorithm);
                id.ifPresent(s -> signatureBuilder.withDigestAlgorithm(s, digestAlgorithm));
                signingCredential.ifPresent(signatureBuilder::withSigningCredential);
                response.setSignature(signatureBuilder.build());
                XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(response).marshall(response);
                if (shouldSign) {
                    Signer.signObject(response.getSignature());
                }
            }
        }
        return response;
    }

    public ResponseBuilder withIssuer(Issuer issuer) {
        this.issuer = ofNullable(issuer);
        return this;
    }

    public ResponseBuilder addEncryptedAssertion(EncryptedAssertion encryptedAssertion) {
        addDefaultEncryptedAssertionIfNoneIsAdded = false;
        this.encryptedAssertions.add(encryptedAssertion);
        return this;
    }

    public ResponseBuilder withSigningCredential(Credential signingCredential) {
        this.signingCredential = ofNullable(signingCredential);
        return this;
    }

    public ResponseBuilder withDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }

    public ResponseBuilder withSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    private static Status getStatus() {
        Status status = testSamlObjectFactory.createStatus();
        StatusCode statusCode = testSamlObjectFactory.createStatusCode();
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        return status;
    }

}
