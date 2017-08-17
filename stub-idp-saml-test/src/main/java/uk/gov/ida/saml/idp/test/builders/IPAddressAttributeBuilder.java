package uk.gov.ida.saml.idp.test.builders;

import com.google.common.base.Optional;
import org.opensaml.saml.saml2.core.Attribute;
import uk.gov.ida.saml.core.IdaConstants;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.extensions.IPAddress;

import static com.google.common.base.Optional.fromNullable;

public class IPAddressAttributeBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<String> value = fromNullable("1.2.3.4");

    public static IPAddressAttributeBuilder anIPAddress() {
        return new IPAddressAttributeBuilder();
    }

    public Attribute build() {

        Attribute ipAddressAttribute = openSamlXmlObjectFactory.createAttribute();
        ipAddressAttribute.setFriendlyName(IdaConstants.Attributes_1_1.IPAddress.FRIENDLY_NAME);
        ipAddressAttribute.setName(IdaConstants.Attributes_1_1.IPAddress.NAME);

        IPAddress ipAddressAttributeValue = openSamlXmlObjectFactory.createIPAddressAttributeValue(value.orNull());

        ipAddressAttribute.getAttributeValues().add(ipAddressAttributeValue);

        return ipAddressAttribute;
    }

    public IPAddressAttributeBuilder withValue(String name) {
        this.value = fromNullable(name);
        return this;
    }
}
