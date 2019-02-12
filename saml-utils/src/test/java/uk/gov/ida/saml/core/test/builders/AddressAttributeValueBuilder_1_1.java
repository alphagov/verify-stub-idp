package uk.gov.ida.saml.core.test.builders;

import org.joda.time.DateTime;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddressAttributeValueBuilder_1_1 {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();

    private Optional<DateTime> from = Optional.empty();
    private Optional<DateTime> to = Optional.empty();

    private List<String> lines = new ArrayList<>();
    private Optional<String> postCode = Optional.of("RG99 1YY");
    private Optional<String> internationalPostCode = Optional.of("RG88 1ZZ");
    private Optional<String> uprn = Optional.of("79347894352");

    private boolean verified = false;

    public static AddressAttributeValueBuilder_1_1 anAddressAttributeValue() {
        return new AddressAttributeValueBuilder_1_1();
    }

    public Address build() {

        Address addressAttributeValue = openSamlXmlObjectFactory.createAddressAttributeValue();

        if (from.isPresent()) {
            addressAttributeValue.setFrom(from.get());
        }
        if (to.isPresent()) {
            addressAttributeValue.setTo(to.get());
        }
        addressAttributeValue.setVerified(verified);

        for (String line : lines) {
            addressAttributeValue.getLines().add(openSamlXmlObjectFactory.createLine(line));
        }
        if (postCode.isPresent()) {
            addressAttributeValue.setPostCode(openSamlXmlObjectFactory.createPostCode(postCode.get()));
        }
        if (internationalPostCode.isPresent()) {
            addressAttributeValue.setInternationalPostCode(openSamlXmlObjectFactory.createInternationalPostCode(internationalPostCode.get()));
        }
        if (uprn.isPresent()) {
            addressAttributeValue.setUPRN(openSamlXmlObjectFactory.createUPRN(uprn.get()));
        }

        return addressAttributeValue;
    }

    public AddressAttributeValueBuilder_1_1 withFrom(DateTime from) {
        this.from = Optional.ofNullable(from);
        return this;
    }

    public AddressAttributeValueBuilder_1_1 withTo(DateTime to) {
        this.to = Optional.ofNullable(to);
        return this;
    }

    public AddressAttributeValueBuilder_1_1 addLines(List<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public AddressAttributeValueBuilder_1_1 withVerified(boolean verified) {
        this.verified = verified;
        return this;
    }

    public AddressAttributeValueBuilder_1_1 withPostcode(String postCode) {
        this.postCode = Optional.ofNullable(postCode);
        return this;
    }

    public AddressAttributeValueBuilder_1_1 withInternationalPostcode(String internationalPostcode) {
        this.internationalPostCode = Optional.ofNullable(internationalPostcode);
        return this;
    }

    public AddressAttributeValueBuilder_1_1 withUprn(String uprn) {
        this.uprn = Optional.ofNullable(uprn);
        return this;
    }
}
