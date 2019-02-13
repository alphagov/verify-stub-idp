package uk.gov.ida.common;

import java.net.URI;

public class ResourceLocationDto {
    private URI target;

    @SuppressWarnings("unused") // NEEDED BY JAXB
    protected ResourceLocationDto() {
    }

    public ResourceLocationDto(URI target) {
        this.target = target;
    }

    public URI getTarget() {
        return target;
    }
}
