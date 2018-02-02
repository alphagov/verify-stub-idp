package uk.gov.ida.stub.idp.services;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import se.litsec.eidas.opensaml.common.EidasConstants;
import se.litsec.eidas.opensaml.ext.RequestedAttribute;
import se.litsec.eidas.opensaml.ext.attributes.AttributeConstants;
import se.litsec.eidas.opensaml.ext.attributes.CurrentFamilyNameType;
import se.litsec.eidas.opensaml.ext.attributes.CurrentGivenNameType;
import se.litsec.eidas.opensaml.ext.attributes.DateOfBirthType;
import se.litsec.eidas.opensaml.ext.attributes.GenderType;
import se.litsec.eidas.opensaml.ext.attributes.PersonIdentifierType;
import uk.gov.ida.notification.saml.translation.EidasAttributeBuilder;
import uk.gov.ida.notification.saml.translation.EidasResponseBuilder;
import uk.gov.ida.stub.idp.StubIdpModule;
import uk.gov.ida.stub.idp.domain.EidasUser;
import uk.gov.ida.stub.idp.domain.SamlResponse;
import uk.gov.ida.stub.idp.repositories.MetadataRepository;
import uk.gov.ida.stub.idp.repositories.Session;
import uk.gov.ida.stub.idp.saml.transformers.EidasResponseTransformerProvider;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EidasSuccessAuthnResponseService {

    private EidasResponseBuilder eidasResponseBuilder;
    private final EidasResponseTransformerProvider eidasResponseTransformerProvider;
    private final MetadataRepository metadataProvider;

    @Inject
    public EidasSuccessAuthnResponseService(EidasResponseBuilder eidasResponseBuilder, EidasResponseTransformerProvider eidasResponseTransformerProvider, @Named(StubIdpModule.HUB_CONNECTOR_METADATA_REPOSITORY) Optional<MetadataRepository> metadataProvider) {
        this.eidasResponseBuilder = eidasResponseBuilder;
        this.eidasResponseTransformerProvider = eidasResponseTransformerProvider;
        this.metadataProvider = metadataProvider.get();
    }

    public SamlResponse getEidasSuccessResponse(Session session){
        URI hubUrl = metadataProvider.getAssertionConsumerServiceLocation();
        String requestId = session.getEidasAuthnRequest().getRequestId();
        List<Attribute> eidasAttributes = getEidasAttributes(session);
        DateTime issueInstant = DateTime.now();

        Response response = eidasResponseBuilder.createEidasResponse(StatusCode.SUCCESS, UUID.randomUUID().toString(),
                EidasConstants.EIDAS_LOA_SUBSTANTIAL, eidasAttributes, requestId, issueInstant, issueInstant, issueInstant, hubUrl.toString());
        String eidasResponse = eidasResponseTransformerProvider.getTransformer().apply(response);

        return new SamlResponse(eidasResponse, session.getRelayState(), hubUrl);
    }

    private List<Attribute> getEidasAttributes(Session session){
        List<RequestedAttribute> requestedAttributes = session.getEidasAuthnRequest().getRequestedAttributes();
        EidasUser user = session.getEidasUser().get();

        List<EidasAttributeBuilder> eidasAttributeBuilders = new ArrayList<>();
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_NAME, AttributeConstants.EIDAS_CURRENT_GIVEN_NAME_ATTRIBUTE_FRIENDLY_NAME, CurrentFamilyNameType.TYPE_NAME, user.getFirstName()));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_NAME, AttributeConstants.EIDAS_CURRENT_FAMILY_NAME_ATTRIBUTE_FRIENDLY_NAME, CurrentGivenNameType.TYPE_NAME, user.getFamilyName()));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_NAME, AttributeConstants.EIDAS_DATE_OF_BIRTH_ATTRIBUTE_FRIENDLY_NAME, DateOfBirthType.TYPE_NAME, user.getDateOfBirth().toString()));
        eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_NAME, AttributeConstants.EIDAS_PERSON_IDENTIFIER_ATTRIBUTE_FRIENDLY_NAME, PersonIdentifierType.TYPE_NAME ,"UK/NL/" + user.getPersistentId()));
        Boolean isGenderRequired = requestedAttributes.stream().anyMatch(attribute -> attribute.getName().equals(AttributeConstants.EIDAS_GENDER_ATTRIBUTE_NAME) && attribute.isRequired());
        if (isGenderRequired && user.getGender().isPresent()) {
            eidasAttributeBuilders.add(new EidasAttributeBuilder(AttributeConstants.EIDAS_GENDER_ATTRIBUTE_NAME, AttributeConstants.EIDAS_GENDER_ATTRIBUTE_FRIENDLY_NAME, GenderType.TYPE_NAME, user.getGender().get().getValue()));
        }
        return eidasAttributeBuilders.stream()
                .map(builder -> builder.build())
                .collect(Collectors.toList());
    }
}
