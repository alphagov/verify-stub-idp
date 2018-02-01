package uk.gov.ida.stub.idp.services;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import se.litsec.eidas.opensaml.common.EidasConstants;
import se.litsec.eidas.opensaml.ext.RequestedAttribute;
import se.litsec.eidas.opensaml.ext.attributes.AttributeConstants;
import se.litsec.eidas.opensaml.ext.attributes.CurrentAddressType;
import se.litsec.eidas.opensaml.ext.attributes.CurrentFamilyNameType;
import se.litsec.eidas.opensaml.ext.attributes.CurrentGivenNameType;
import se.litsec.eidas.opensaml.ext.attributes.DateOfBirthType;
import se.litsec.eidas.opensaml.ext.attributes.GenderType;
import se.litsec.eidas.opensaml.ext.attributes.PersonIdentifierType;
import uk.gov.ida.notification.saml.ResponseAssertionEncrypter;
import uk.gov.ida.notification.saml.translation.EidasAttributeBuilder;
import uk.gov.ida.notification.saml.translation.EidasResponseBuilder;
import uk.gov.ida.saml.core.domain.IdentityProviderAssertion;
import uk.gov.ida.saml.core.domain.IpAddress;
import uk.gov.ida.saml.core.domain.PersistentId;
import uk.gov.ida.stub.idp.domain.IdpUser;
import uk.gov.ida.stub.idp.domain.OutboundResponseFromIdp;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.domain.factories.AssertionRestrictionsFactory;
import uk.gov.ida.stub.idp.domain.factories.IdentityProviderAssertionFactory;
import uk.gov.ida.stub.idp.domain.factories.MatchingDatasetFactory;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;
import uk.gov.ida.stub.idp.saml.transformers.OutboundResponseFromIdpTransformerProvider;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement.createIdentityProviderAuthnStatement;

public class SuccessAuthnResponseService {

    private final IdentityProviderAssertionFactory identityProviderAssertionFactory;
    private final IdpStubsRepository idpStubsRepository;
    private final MetadataRepository metadataProvider;
    private final AssertionRestrictionsFactory assertionRestrictionsFactory;
    private final OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider;
    private EidasResponseBuilder eidasResponseBuilder;
    private final EidasResponseTransformerProvider eidasResponseTransformerProvider;

    @Inject
    public SuccessAuthnResponseService(
            IdentityProviderAssertionFactory identityProviderAssertionFactory,
            IdpStubsRepository idpStubsRepository,
            MetadataRepository metadataProvider,
            AssertionRestrictionsFactory assertionRestrictionsFactory,
            OutboundResponseFromIdpTransformerProvider outboundResponseFromIdpTransformerProvider,
            EidasResponseBuilder eidasResponseBuilder,
            EidasResponseTransformerProvider eidasResponseTransformerProvider) {

        this.identityProviderAssertionFactory = identityProviderAssertionFactory;
        this.idpStubsRepository = idpStubsRepository;
        this.metadataProvider = metadataProvider;
        this.assertionRestrictionsFactory = assertionRestrictionsFactory;
        this.outboundResponseFromIdpTransformerProvider = outboundResponseFromIdpTransformerProvider;
        this.eidasResponseBuilder = eidasResponseBuilder;
        this.eidasResponseTransformerProvider = eidasResponseTransformerProvider;
    }

    public SamlResponse getSuccessResponse(boolean randomisePid, String remoteIpAddress, String idpName, Session session) {
        URI hubUrl = metadataProvider.getAssertionConsumerServiceLocation();
        Idp idp = idpStubsRepository.getIdpWithFriendlyId(idpName);

        IdpUser idpUser = session.getIdpUser().get();
        String requestId = session.getIdaAuthnRequestFromHub().getId();

        PersistentId persistentId = new PersistentId(idpUser.getPersistentId());
        if(randomisePid) {
            persistentId = new PersistentId(UUID.randomUUID().toString());
        }

        IdentityProviderAssertion matchingDatasetAssertion = identityProviderAssertionFactory.createMatchingDatasetAssertion(
                persistentId,
                idp.getIssuerId(),
                MatchingDatasetFactory.create(idpUser),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        IdentityProviderAssertion authnStatementAssertion = identityProviderAssertionFactory.createAuthnStatementAssertion(
                persistentId,
                idp.getIssuerId(),
                createIdentityProviderAuthnStatement(idpUser.getLevelOfAssurance(), new IpAddress(remoteIpAddress)),
                assertionRestrictionsFactory.createRestrictionsForSendingToHub(requestId));

        OutboundResponseFromIdp idaResponse = OutboundResponseFromIdp.createSuccessResponseFromIdp(
                session.getIdaAuthnRequestFromHub().getId(),
                idp.getIssuerId(),
                matchingDatasetAssertion,
                authnStatementAssertion,
                hubUrl);
        String idpResponse = outboundResponseFromIdpTransformerProvider.get(idp).apply(idaResponse);

        return new SamlResponse(idpResponse, session.getRelayState(), hubUrl);
    }

    public SamlResponse getEidasSuccessResponse(Session session){
        URI hubUrl = metadataProvider.getAssertionConsumerServiceLocation();
        String requestId = session.getEidasAuthnRequest().getRequestId();
        List<Attribute> eidasAttributes = getEidasAttributes(session.getEidasAuthnRequest().getRequestedAttributes());
        DateTime issueInstant = DateTime.now();

        Response response = eidasResponseBuilder.createEidasResponse(StatusCode.SUCCESS, UUID.randomUUID().toString(), EidasConstants.EIDAS_LOA_SUBSTANTIAL, eidasAttributes, requestId, issueInstant, issueInstant, issueInstant );
        String eidasResponse = eidasResponseTransformerProvider.getTransformer().apply(response);

        return new SamlResponse(eidasResponse, session.getRelayState(), hubUrl);
    }

    private List<Attribute> getEidasAttributes(List<RequestedAttribute> requestedAttributes){
        List<EidasAttributeBuilder> eidasAttributeBuilders = new ArrayList<>();
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_NAME, AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_FRIENDLY_NAME, CurrentFamilyNameType.TYPE_NAME ,"Bob"));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_NAME, AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_FRIENDLY_NAME, CurrentGivenNameType.TYPE_NAME ,"Hobbs"));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_NAME, AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_FRIENDLY_NAME, DateOfBirthType.TYPE_NAME ,"1985-01-30"));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_NAME, AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_FRIENDLY_NAME, PersonIdentifierType.TYPE_NAME ,"UK/NL/pid"));
        Boolean isGenderRequired = requestedAttributes.stream().anyMatch(attribute -> attribute.getName().equals(AttributeConstants.EIDAS_GENDER_ATTRIBUTE_NAME) && attribute.isRequired());
        if (isGenderRequired){
            eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_GENDER_ATTRIBUTE_NAME, AttributeConstants.EIDAS_GENDER_ATTRIBUTE_FRIENDLY_NAME, GenderType.TYPE_NAME, "Male"));
        }
        return eidasAttributeBuilders.stream()
                .map(builder -> builder.build())
                .collect(Collectors.toList());
    }


}
