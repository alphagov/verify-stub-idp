package uk.gov.ida.common.shared.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PrivateKeyConfigurationDeserializer extends JsonDeserializer<PrivateKeyConfiguration> {
    @Override
    public PrivateKeyConfiguration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Setting the Codec explicitly is needed when this executes with the YAMLParser
        // for example, when our Dropwizard apps start. The codec doesn't need to be set
        // when the JsonParser implementation is used.
        p.setCodec(new ObjectMapper());
        JsonNode node = p.getCodec().readTree(p);

        JsonNode certFileNode = node.get("keyFile");
        if(certFileNode != null) {
            return p.getCodec().treeToValue(node, PrivateKeyFileConfiguration.class);
        }
        return p.getCodec().treeToValue(node, EncodedPrivateKeyConfiguration.class);
    }
}
