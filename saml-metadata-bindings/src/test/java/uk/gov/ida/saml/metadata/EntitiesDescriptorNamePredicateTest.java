package uk.gov.ida.saml.metadata;

import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.soap.wsaddressing.impl.AddressBuilder;
import org.opensaml.xmlsec.signature.support.SignatureException;
import uk.gov.ida.saml.metadata.test.factories.metadata.EntityDescriptorFactory;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.builders.metadata.EntitiesDescriptorBuilder.anEntitiesDescriptor;

public class EntitiesDescriptorNamePredicateTest {

    @Before
    public void setUp() throws InitializationException {
        InitializationService.initialize();
    }

    @Test
    public void shouldApplyForEntityWithExpectedParent() throws MarshallingException, SignatureException {
        String entitiesName = "collection of entities";

        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().idpEntityDescriptor("an idp");
        anEntitiesDescriptor()
                .withEntityDescriptors(Collections.singletonList(entityDescriptor))
                .withName(entitiesName)
                .build();

        EntitiesDescriptorNamePredicate entitiesDescriptorNamePredicate = new EntitiesDescriptorNamePredicate(
                new EntitiesDescriptorNameCriterion(entitiesName));

        assertThat(entitiesDescriptorNamePredicate.apply(entityDescriptor)).isTrue();

    }

    @Test
    public void shouldNotApplyForEntityWithWrongParent() throws MarshallingException, SignatureException {
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().idpEntityDescriptor("an idp");
        anEntitiesDescriptor()
                .withEntityDescriptors(Collections.singletonList(entityDescriptor))
                .withName("collection of entities")
                .build();

        EntitiesDescriptorNamePredicate entitiesDescriptorNamePredicate = new EntitiesDescriptorNamePredicate(
                new EntitiesDescriptorNameCriterion("some other parent"));

        assertThat(entitiesDescriptorNamePredicate.apply(entityDescriptor)).isFalse();
    }

    @Test
    public void shouldNotApplyForEntityWithNoParent() throws MarshallingException, SignatureException {
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().idpEntityDescriptor("an idp");

        EntitiesDescriptorNamePredicate entitiesDescriptorNamePredicate = new EntitiesDescriptorNamePredicate(
                new EntitiesDescriptorNameCriterion("some other parent"));

        assertThat(entitiesDescriptorNamePredicate.apply(entityDescriptor)).isFalse();
    }

    @Test
    public void shouldNotApplyForEntityWithNamelessParent() throws MarshallingException, SignatureException {
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().idpEntityDescriptor("an idp");
        anEntitiesDescriptor()
                .withEntityDescriptors(Collections.singletonList(entityDescriptor))
                .withName(null)
                .build();

        EntitiesDescriptorNamePredicate entitiesDescriptorNamePredicate = new EntitiesDescriptorNamePredicate(
                new EntitiesDescriptorNameCriterion("some other parent"));

        assertThat(entitiesDescriptorNamePredicate.apply(entityDescriptor)).isFalse();
    }

    @Test
    public void shouldNotApplyForEntityWithWrongParentType() throws MarshallingException, SignatureException {
        EntityDescriptor entityDescriptor = new EntityDescriptorFactory().idpEntityDescriptor("an idp");
        entityDescriptor.setParent(new AddressBuilder().buildObject("Some", "Other", "Type"));

        EntitiesDescriptorNamePredicate entitiesDescriptorNamePredicate = new EntitiesDescriptorNamePredicate(
                new EntitiesDescriptorNameCriterion("some other parent"));

        assertThat(entitiesDescriptorNamePredicate.apply(entityDescriptor)).isFalse();
    }

}
