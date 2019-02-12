package uk.gov.ida.saml.core.transformers.inbound;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.domain.Cycle3Dataset;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.test.SamlTransformationErrorManagerTestHelper;
import uk.gov.ida.saml.core.test.builders.AssertionBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.SimpleStringAttributeBuilder.aSimpleStringAttribute;
import static uk.gov.ida.saml.hub.errors.SamlTransformationErrorFactory.missingAttributeStatementInAssertion;

@RunWith(OpenSAMLMockitoRunner.class)
public class Cycle3DatasetFactoryTest {

    private Cycle3DatasetFactory cycle3DatasetFactory;

    @Before
    public void setup() {
        cycle3DatasetFactory = new Cycle3DatasetFactory();
    }

    @Test
    public void transform_shouldTransformAListOfAttributesToACycle3Dataset() throws Exception {
        String attributeNameOne = "attribute name one";
        String attributeNameTwo = "attribute name two";
        String attributeValueOne = "attribute value one";
        String attributeValueTwo = "attribute value two";

        List<Attribute> attributes = new ArrayList<>();
        attributes.add(aSimpleStringAttribute().withName(attributeNameOne).withSimpleStringValue(attributeValueOne).build());
        attributes.add(aSimpleStringAttribute().withName(attributeNameTwo).withSimpleStringValue(attributeValueTwo).build());

        Assertion assertion = AssertionBuilder.aCycle3DatasetAssertion(attributes).buildUnencrypted();

        Cycle3Dataset cycle3Dataset = cycle3DatasetFactory.createCycle3DataSet(assertion);

        assertThat(cycle3Dataset).isNotNull();
        assertThat(cycle3Dataset.getAttributes().size()).isEqualTo(2);
        assertThat(cycle3Dataset.getAttributes().get(attributeNameOne)).isEqualTo(attributeValueOne);
        assertThat(cycle3Dataset.getAttributes().get(attributeNameTwo)).isEqualTo(attributeValueTwo);
    }

    @Test
    public void transform_shouldThrowExceptionIfThereIsMoreThanOneAttributeStatement() throws Exception {
        final Assertion assertion = AssertionBuilder.anAssertion()
                .addAttributeStatement(anAttributeStatement().build())
                .addAttributeStatement(anAttributeStatement().build())
                .buildUnencrypted();

        SamlTransformationErrorManagerTestHelper.validateFail(
                () -> cycle3DatasetFactory.createCycle3DataSet(assertion),
                missingAttributeStatementInAssertion(assertion.getID()));
    }

    @Test
    public void transform_shouldThrowExceptionIfThereIsNoAttributeStatement() throws Exception {
        final Assertion assertion = AssertionBuilder.anAssertion()
                .buildUnencrypted();
        SamlTransformationErrorManagerTestHelper.validateFail(
                () -> cycle3DatasetFactory.createCycle3DataSet(assertion),
                missingAttributeStatementInAssertion(assertion.getID()));
    }
}
