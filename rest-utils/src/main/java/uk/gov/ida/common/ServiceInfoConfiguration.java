package uk.gov.ida.common;

public class ServiceInfoConfiguration {

    protected String name;

    @SuppressWarnings("unused") //Needed by JAXB
    protected ServiceInfoConfiguration() {
    }

    public ServiceInfoConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
