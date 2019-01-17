package uk.gov.ida.stub.idp.csrf;

import javax.ws.rs.POST;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class CSRFCheckProtectionFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if ((resourceInfo.getResourceMethod().getAnnotation(CSRFCheckProtection.class) != null ||
                resourceInfo.getResourceClass().getAnnotation(CSRFCheckProtection.class) != null) &&
                (resourceInfo.getResourceMethod().getAnnotation(POST.class) != null ||
                        resourceInfo.getResourceClass().getAnnotation(POST.class) != null)) {
            context.register(CSRFCheckProtectionFilter.class);
        }
    }
}
