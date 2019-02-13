package uk.gov.ida.resources;

import javax.inject.Inject;
import org.apache.http.HttpStatus;
import uk.gov.ida.common.CommonUrls;
import uk.gov.ida.configuration.ServiceStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(CommonUrls.SERVICE_STATUS)
public class ServiceStatusResource {

    private ServiceStatus serviceStatus;

    @Inject
    public ServiceStatusResource() {

        this.serviceStatus = serviceStatus.getInstance();
    }

    @GET
    public Response isOnline(){
        if (serviceStatus.isServerStatusOK()){
            return Response.ok().build();
        } else {
            return Response.status(HttpStatus.SC_SERVICE_UNAVAILABLE).build();
        }
    }
}
