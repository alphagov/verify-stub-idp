package uk.gov.ida.saml.core.transformers.outbound;

import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.IdaResponse;

import javax.inject.Inject;
import java.util.function.Function;

public abstract class IdaResponseToSamlResponseTransformer<TInput extends IdaResponse> implements Function<TInput,Response> {

    private final OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    @Inject
    public IdaResponseToSamlResponseTransformer(OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    @Override
    public Response apply(TInput originalResponse) {
        Response transformedResponse = openSamlXmlObjectFactory.createResponse();

        transformedResponse.setID(originalResponse.getId());
        transformedResponse.setIssueInstant(originalResponse.getIssueInstant());
        transformedResponse.setInResponseTo(originalResponse.getInResponseTo());

        transformIssuer(originalResponse, transformedResponse);

        transformDestination(originalResponse, transformedResponse);

        transformAssertions(originalResponse, transformedResponse);
        transformedResponse.setStatus(transformStatus(originalResponse));

        return transformedResponse;
    }

    protected void transformIssuer(final TInput originalResponse, final Response transformedResponse) {
        Issuer issuer = openSamlXmlObjectFactory.createIssuer(originalResponse.getIssuer());
        issuer.setFormat(Issuer.ENTITY);
        transformedResponse.setIssuer(issuer);
    }

    protected abstract void transformAssertions(TInput originalResponse, Response transformedResponse);

    protected abstract Status transformStatus(TInput originalResponse);

    protected abstract void transformDestination(TInput originalResponse, Response transformedResponse);

}
