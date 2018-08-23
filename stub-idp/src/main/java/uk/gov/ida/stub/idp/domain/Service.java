package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Service {
    @JsonProperty
    private String name;
    @JsonProperty
    private String loa;
    @JsonProperty
    private String entityId;
    @JsonProperty
    private String serviceCategory;

    public Service() {

    }

    public Service(String name, String loa, String entityId, String serviceCategory) {
        this.name = name;
        this.loa = loa;
        this.entityId = entityId;
        this.serviceCategory = serviceCategory;
    }

    public String getName() {
        return name;
    }

    public String getLoa() {
        return loa;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }
}
