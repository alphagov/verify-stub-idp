package uk.gov.ida.resources;

import org.junit.Test;
import uk.gov.ida.common.VersionInfoDto;

public class VersionInfoResourceTest {

    @Test
    public void shouldLoadDataFromManifest() throws Exception {
        VersionInfoResource versionInfoResource = new VersionInfoResource();

        VersionInfoDto versionInfo = versionInfoResource.getVersionInfo();
        // note this should not throw an exception... but setting it up to test is hard :-(
    }

}
