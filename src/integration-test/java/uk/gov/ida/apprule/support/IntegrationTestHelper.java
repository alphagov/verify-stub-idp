package uk.gov.ida.apprule.support;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

public class IntegrationTestHelper {
    static {
        //doALittleHackToMakeGuicierHappy
        // magically, this has to be the first test to run otherwise things will fail.
        // see:
        // - https://github.com/HubSpot/dropwizard-guice/issues/95
        // - https://github.com/Squarespace/jersey2-guice/pull/39
        JerseyGuiceUtils.reset();
    }
}
