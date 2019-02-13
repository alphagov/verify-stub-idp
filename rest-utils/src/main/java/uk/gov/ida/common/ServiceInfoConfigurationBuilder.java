package uk.gov.ida.common;

public class ServiceInfoConfigurationBuilder {

    private String name = "service name";

    public static ServiceInfoConfigurationBuilder aServiceInfo() {
        return new ServiceInfoConfigurationBuilder();
    }

    public ServiceInfoConfiguration build() {
        return new TestServiceInfoConfiguration(name);
    }

    public ServiceInfoConfigurationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    private static class TestServiceInfoConfiguration extends ServiceInfoConfiguration {
        private TestServiceInfoConfiguration(String name) {
            this.name = name;
        }
    }

}
