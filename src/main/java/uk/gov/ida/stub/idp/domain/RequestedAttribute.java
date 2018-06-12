package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestedAttribute {

    private final String name;
    private final Boolean required;

    @JsonCreator
    public RequestedAttribute(@JsonProperty("name") String name, @JsonProperty("required") Boolean required) {

        this.name = name;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public Boolean isRequired() {
        return required;
    }
}
