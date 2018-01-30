package uk.gov.ida.apprule;

import org.junit.Before;
import org.junit.ClassRule;
import uk.gov.ida.apprule.support.StubIdpAppRule;

import static uk.gov.ida.stub.idp.builders.StubIdpBuilder.aStubIdp;

public class EidasUserLogsInIntegrationTests {

    private static final String COUNTRY_NAME = "country1";
    public static final String DISPLAY_NAME = "User Repository Identity Service";

    @ClassRule
    public static final StubIdpAppRule applicationRule = new StubIdpAppRule()
            .withStubIdp(aStubIdp()
                    .withId(COUNTRY_NAME)
                    .withDisplayName(DISPLAY_NAME)
                    .build());

    @Before
    public void setUp() {
    }


}
