package uk.gov.ida.stub.idp.services;

import org.apache.log4j.Logger;
import uk.gov.ida.jerseyclient.JsonClient;
import uk.gov.ida.stub.idp.configuration.SingleIdpConfiguration;
import uk.gov.ida.stub.idp.domain.Service;
import uk.gov.ida.stub.idp.exceptions.FeatureNotEnabledException;

import javax.inject.Inject;
import javax.ws.rs.core.GenericType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ServiceListService {

    private final Logger LOG = Logger.getLogger(ServiceListService.class);

    private final SingleIdpConfiguration singleIdpConfiguration;
    private final JsonClient jsonClient;

    @Inject
    public ServiceListService(SingleIdpConfiguration singleIdpConfiguration, JsonClient jsonClient) {
        this.singleIdpConfiguration = singleIdpConfiguration;
        this.jsonClient = jsonClient;
    }

    public List<Service> getServices() throws FeatureNotEnabledException {

        if (!singleIdpConfiguration.isEnabled()) throw new FeatureNotEnabledException();

        return readListFromHub();
    }

    private List<Service> readListFromHub(){
        try {

            return jsonClient.get(singleIdpConfiguration.getServiceListUri(), new GenericType<List<Service>>() {});
        } catch (RuntimeException ex) {
            LOG.error(MessageFormat.format("Error getting service list from {0}", singleIdpConfiguration.getServiceListUri().toString()), ex);
        }
        return new ArrayList<Service>() {};
    }
}
