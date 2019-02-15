package uk.gov.ida.stub.idp.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseConfiguration {


    @JsonProperty("url")
    private String url;


    @JsonProperty("vcapServices")
    private String vcapServices;

    private String getUrlFromVcap(String vcapServices) {
        return Optional
                .ofNullable(vcapServices)
                .map(val -> {
                    try {
                        ObjectMapper mapper = Jackson.newObjectMapper();
                        return mapper.readTree(val);
                    }
                    catch (IOException e) {
                        throw new RuntimeException("IOException when parsing VCAP_SERVICES environment variable");
                    }
                })
                .map(vcap -> vcap.get("postgres"))
                .map(postgresDatabases -> postgresDatabases.get(0))
                .map(postgresDatabase -> postgresDatabase.get("credentials"))
                .map(credentials -> credentials.get("jdbcuri"))
                .map(JsonNode::textValue)
                .orElseThrow(() -> new RuntimeException("Could not parse vcap services"));
    }

    public String getUrl() {
        if (!Strings.isNullOrEmpty(vcapServices)) {
            return getUrlFromVcap(vcapServices);
        } else if (!Strings.isNullOrEmpty(url)) {
            return url;
        }

        throw new RuntimeException("Neither url nor vcapServices was workable in database configuration");
    }
}
