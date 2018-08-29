package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
    @JsonProperty
    private String name;
    @JsonProperty
    private String loa;

    @JsonProperty
    private String serviceId;
    @JsonProperty
    private String serviceCategory;

    public Service() {

    }

    public Service(String name, String loa, String serviceId, String serviceCategory) {
        this.name = name;
        this.loa = loa;
        this.serviceId = serviceId;
        this.serviceCategory = serviceCategory;
    }

    public String getName() {
        return name;
    }

    public String getLoa() {
        return loa;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public String getServiceId() {
        return serviceId;
    }
}
