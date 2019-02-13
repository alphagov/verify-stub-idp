package uk.gov.ida.resources;


import com.google.common.base.Throwables;
import uk.gov.ida.common.CommonUrls;
import uk.gov.ida.common.VersionInfoDto;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

// See http://stackoverflow.com/questions/1272648/reading-my-own-jars-manifest for implementation details.
@Path(CommonUrls.VERSION_INFO_ROOT)
@Produces(MediaType.APPLICATION_JSON)
public class VersionInfoResource {

    @GET
    public VersionInfoDto getVersionInfo() {
        Attributes manifest = getManifest();
        String buildNumber = manifest.getValue("Build-Number");
        String gitCommit = manifest.getValue("Git-Commit");
        String buildTimestamp = manifest.getValue("Build-Timestamp");

        return new VersionInfoDto(buildNumber, gitCommit, buildTimestamp);
    }

    private Attributes getManifest() {
        Manifest manifest;
        try {
            manifest = new Manifest(getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return manifest.getMainAttributes();
    }

}
