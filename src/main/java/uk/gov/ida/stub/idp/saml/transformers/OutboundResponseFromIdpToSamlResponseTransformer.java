package uk.gov.ida.stub.idp.saml.transformers;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.transformers.outbound.IdaResponseToSamlResponseTransformer;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAssertionToAssertionTransformer;
import uk.gov.ida.stub.idp.domain.IdpIdaStatusMarshaller;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;

import java.util.Optional;

public class OutboundResponseFromIdpToSamlResponseTransformer extends IdaResponseToSamlResponseTransformer<OutboundResponseFromIdp> {

    private static final Logger LOG = LoggerFactory.getLogger(OutboundResponseFromIdpToSamlResponseTransformer.class);
    private final IdentityProviderAssertionToAssertionTransformer assertionTransformer;
    private final IdpIdaStatusMarshaller statusMarshaller;

    public OutboundResponseFromIdpToSamlResponseTransformer(
            IdpIdaStatusMarshaller statusMarshaller,
            OpenSamlXmlObjectFactory openSamlXmlObjectFactory,
            IdentityProviderAssertionToAssertionTransformer assertionTransformer) {

        super(openSamlXmlObjectFactory);

        this.statusMarshaller = statusMarshaller;
        this.assertionTransformer = assertionTransformer;
    }

    @Override
    public void transformAssertions(OutboundResponseFromIdp originalResponse, Response transformedResponse) {
        Optional<IdentityProviderAssertion> matchingDatasetAssertion = originalResponse.getMatchingDatasetAssertion();
        if (matchingDatasetAssertion.isPresent()) {
            Assertion transformedAssertion = assertionTransformer.transform(matchingDatasetAssertion.get());
            transformedResponse.getAssertions().add(transformedAssertion);
        } else {
            LOG.warn("Response From Idp created without an MDS - this is probably a mistake");
        }

        Optional<IdentityProviderAssertion> authnStatementAssertion = originalResponse.getAuthnStatementAssertion();
        if (authnStatementAssertion.isPresent()) {
            Assertion transformedAssertion = assertionTransformer.transform(authnStatementAssertion.get());
            transformedResponse.getAssertions().add(transformedAssertion);
        } else {
            LOG.warn("Response From Idp created without an AuthnContext - this is probably a mistake");
        }
    }

    @Override
    protected Status transformStatus(OutboundResponseFromIdp originalResponse) {
        return statusMarshaller.toSamlStatus(originalResponse.getStatus());

    }

    @Override
    protected void transformDestination(OutboundResponseFromIdp originalResponse, Response transformedResponse) {
        transformedResponse.setDestination(originalResponse.getDestination().toASCIIString());
    }
}
