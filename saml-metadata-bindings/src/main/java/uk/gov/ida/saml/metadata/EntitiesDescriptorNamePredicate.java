package uk.gov.ida.saml.metadata;

import com.google.common.base.Predicate;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

public class EntitiesDescriptorNamePredicate implements Predicate<EntityDescriptor> {

    private final EntitiesDescriptorNameCriterion criterion;

    public EntitiesDescriptorNamePredicate(EntitiesDescriptorNameCriterion criterion) {
        this.criterion = criterion;
    }

    @Override
    public boolean apply(EntityDescriptor input) {
        XMLObject parent = input.getParent();
        if (parent == null || !(parent instanceof EntitiesDescriptor)) {
            return false;
        }
        String entitiesName = ((EntitiesDescriptor) parent).getName();
        return entitiesName != null && entitiesName.equals(criterion.getExpectedName());
    }

}
