package uk.gov.ida.saml.core.test.builders;

import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.DigestAlgorithm;
import org.opensaml.xmlsec.algorithm.SignatureAlgorithm;
import org.opensaml.xmlsec.algorithm.descriptors.DigestSHA256;
import org.opensaml.xmlsec.algorithm.descriptors.SignatureRSASHA256;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.core.test.builders.IssuerBuilder.anIssuer;

public class ResponseBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

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

    private Optional<Issuer> issuer = ofNullable(anIssuer().build());
    private Optional<String> id = ofNullable(DEFAULT_RESPONSE_ID);
    private Optional<DateTime> issueInstant = ofNullable(DateTime.now());
    private Optional<String> inResponseTo = ofNullable(DEFAULT_REQUEST_ID);
    private Optional<Status> status = ofNullable(StatusBuilder.aStatus().build());
    private Optional<Credential> signingCredential = empty();
    private Optional<String > destination = ofNullable("http://destination.com");

    public static ResponseBuilder aResponse() {
        ResponseBuilder responseBuilder = new ResponseBuilder();
        responseBuilder.defaultEncryptedAssertion = anAssertion().build();
        return responseBuilder;
    }

    public static ResponseBuilder aValidIdpResponse() {
        return aResponse()
                .withStatus(StatusBuilder.aStatus().build())
                .addAssertion(AssertionBuilder.anAssertion().addAttributeStatement(AttributeStatementBuilder.anAttributeStatement().build()).buildUnencrypted())
                .addAssertion(AssertionBuilder.anAssertion().addAuthnStatement(AuthnStatementBuilder.anAuthnStatement().build()).buildUnencrypted());
    }

    public Response build() throws MarshallingException, SignatureException {

        Response response = openSamlXmlObjectFactory.createResponse();
        if (id.isPresent()) {
            response.setID(id.get());
        }
        if (inResponseTo.isPresent()) {
            response.setInResponseTo(inResponseTo.get());
        }
        if (issueInstant.isPresent()) {
            response.setIssueInstant(issueInstant.get());
        }
        if (status.isPresent()) {
            response.setStatus(status.get());
        }

        if (destination.isPresent()) {
            response.setDestination(destination.get());
        }

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
                if (id.isPresent()) {
                    signatureBuilder.withDigestAlgorithm(id.get(), digestAlgorithm);
                }
                if (signingCredential.isPresent()){
                    signatureBuilder.withSigningCredential(signingCredential.get());
                }
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

    public ResponseBuilder withId(String id) {
        this.id = ofNullable(id);
        return this;
    }

    public ResponseBuilder withInResponseTo(String requestId) {
        this.inResponseTo = ofNullable(requestId);
        return this;
    }

    public ResponseBuilder withIssueInstant(DateTime issueInstant) {
        this.issueInstant = ofNullable(issueInstant);
        return this;
    }

    public ResponseBuilder addAssertion(Assertion assertion) {
        addDefaultEncryptedAssertionIfNoneIsAdded = false;
        this.assertions.add(assertion);
        return this;
    }

    public ResponseBuilder withNoDefaultAssertion() {
        this.addDefaultEncryptedAssertionIfNoneIsAdded = false;
        return this;
    }

    public ResponseBuilder addEncryptedAssertion(EncryptedAssertion encryptedAssertion) {
        addDefaultEncryptedAssertionIfNoneIsAdded = false;
        this.encryptedAssertions.add(encryptedAssertion);
        return this;
    }

    public ResponseBuilder withoutSignatureElement() {
        shouldAddSignature = false;
        return this;
    }

    public ResponseBuilder withoutSigning() {
        shouldSign = false;
        return this;
    }

    public ResponseBuilder withStatus(Status status) {
        this.status = ofNullable(status);
        return this;
    }

    public ResponseBuilder withSigningCredential(Credential signingCredential) {
        this.signingCredential = ofNullable(signingCredential);
        return this;
    }

    public ResponseBuilder withDestination(String destination) {
        this.destination = ofNullable(destination);
        return this;
    }

    public ResponseBuilder withSignatureAlgorithm(@NotNull SignatureAlgorithm signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
        return this;
    }

    public ResponseBuilder withDigestAlgorithm(@NotNull DigestAlgorithm digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
        return this;
    }
}
