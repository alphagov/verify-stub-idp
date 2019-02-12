package uk.gov.ida.saml.hub.domain;

import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.joda.time.DateTime;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.HubAssertion;
import uk.gov.ida.saml.core.domain.PersistentId;

import java.net.URI;
import java.util.List;

public class HubAttributeQueryRequest extends BaseHubAttributeQueryRequest {
    private String encryptedAuthnAssertion;
    private Optional<HubAssertion> cycle3AttributeAssertion;
    private Optional<List<UserAccountCreationAttribute>> userAccountCreationAttributes;
    private AuthnContext authnContext;
    @NotNull
    private String encryptedMatchingDatasetAssertion;

    public HubAttributeQueryRequest(
            String id,
            PersistentId persistentId,
            String encryptedMatchingDatasetAssertion,
            String encryptedAuthnAssertion,
            Optional<HubAssertion> cycle3AttributeAssertion,
            Optional<List<UserAccountCreationAttribute>> userAccountCreationAttributes,
            DateTime issueInstant,
            URI assertionConsumerServiceUrl,
            String authnRequestIssuerEntityId,
            AuthnContext authnContext,
            String hubEntityId) {
        super(id, hubEntityId, issueInstant, null, persistentId, assertionConsumerServiceUrl, authnRequestIssuerEntityId);
        this.encryptedAuthnAssertion = encryptedAuthnAssertion;
        this.cycle3AttributeAssertion = cycle3AttributeAssertion;
        this.userAccountCreationAttributes = userAccountCreationAttributes;
        this.authnContext = authnContext;
        this.encryptedMatchingDatasetAssertion = encryptedMatchingDatasetAssertion;
    }


    public Optional<HubAssertion> getCycle3AttributeAssertion() {
        return cycle3AttributeAssertion;
    }

    public Optional<List<UserAccountCreationAttribute>> getUserAccountCreationAttributes() {
        return userAccountCreationAttributes;
    }

    public String getEncryptedAuthnAssertion() {
        return encryptedAuthnAssertion;
    }

    public AuthnContext getAuthnContext() {
        return authnContext;
    }

    public String getEncryptedMatchingDatasetAssertion() {
        return encryptedMatchingDatasetAssertion;
    }
}
