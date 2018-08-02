package uk.gov.ida.stub.idp.filters;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class SessionCookieValueMustExistAsASessionFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceMethod().getAnnotation(SessionCookieValueMustExistAsASession.class) != null ||
                resourceInfo.getResourceClass().getAnnotation(SessionCookieValueMustExistAsASession.class) != null) {
            context.register(SessionCookieValueMustExistAsASessionFilter.class);
        }
    }
}
