package uk.gov.ida.stub.idp.views;

import java.nio.charset.StandardCharsets;

public class FeatureNotEnabledPageView extends ErrorPageView {

    public FeatureNotEnabledPageView() {
        super("featureNotEnabledPage.ftl", StandardCharsets.UTF_8);
    }

}
