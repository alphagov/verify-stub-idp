package uk.gov.ida.dropwizard.logstash.support;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class RootResource {
    @GET
    public String get() {
        return "hello!";
    }
}
