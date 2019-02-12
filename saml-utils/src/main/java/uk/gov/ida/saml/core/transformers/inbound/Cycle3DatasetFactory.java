package uk.gov.ida.saml.core.transformers.inbound;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import uk.gov.ida.saml.core.domain.Cycle3Dataset;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.ida.saml.hub.errors.SamlTransformationErrorFactory.missingAttributeStatementInAssertion;

public class Cycle3DatasetFactory {

    public Cycle3Dataset createCycle3DataSet(Assertion assertion) {
        List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();

        if (attributeStatements.size() != 1) {
            SamlValidationSpecificationFailure failure = missingAttributeStatementInAssertion(assertion.getID());
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        List<Attribute> attributes = attributeStatements.get(0).getAttributes();
        Map<String, String> data = new HashMap<>();
        for (Attribute attribute : attributes) {
            data.put(attribute.getName(), ((StringBasedMdsAttributeValue)attribute.getAttributeValues().get(0)).getValue());
        }

        return Cycle3Dataset.createFromData(data);
    }
}
