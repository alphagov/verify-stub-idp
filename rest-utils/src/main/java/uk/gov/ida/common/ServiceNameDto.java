package uk.gov.ida.common;

public class ServiceNameDto {

    private String serviceName;

    // Needed for JAXB
    public ServiceNameDto() {}

    public ServiceNameDto(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
