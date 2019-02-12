package uk.gov.ida.saml.security.saml.builders;

import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Conditions;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class ConditionsBuilder {

    private TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    private List<AudienceRestriction> audienceRestrictions = new ArrayList<>();
    private AudienceRestriction defaultAudienceRestriction = testSamlObjectFactory.createAudienceRestriction(TestEntityIds.HUB_ENTITY_ID);
    private boolean shouldIncludeDefaultAudienceRestriction = true;

    public static ConditionsBuilder aConditions() {
        return new ConditionsBuilder();
    }

    public Conditions build() {
        Conditions conditions = testSamlObjectFactory.createConditions();

        if (shouldIncludeDefaultAudienceRestriction) {
            audienceRestrictions.add(defaultAudienceRestriction);
        }
        conditions.getAudienceRestrictions().addAll(audienceRestrictions);

        return conditions;
    }
}
