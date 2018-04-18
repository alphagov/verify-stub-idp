package uk.gov.ida.stub.idp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.glassfish.jersey.internal.util.Base64;

import java.text.MessageFormat;

public class EidasAddress {
    private final String poBox;
    private final String locatorDesignator;
    private final String locatorName;
    private final String cvAddressArea;
    private final String thoroughfare;
    private final String postName;
    private final String adminunitFirstLine;
    private final String adminunitSecondLine;
    private final String postCode;

    @JsonCreator
    public EidasAddress(@JsonProperty("poBox") String poBox, @JsonProperty("locatorDesignator") String locatorDesignator, @JsonProperty("locatorName") String locatorName, @JsonProperty("cvAddressArea") String cvAddressArea, @JsonProperty("thoroughfare") String thoroughfare, @JsonProperty("postName") String postName, @JsonProperty("adminunitFirstLine") String adminunitFirstLine, @JsonProperty("adminunitSecondLine") String adminunitSecondLine, @JsonProperty("postCode") String postCode) {
        this.poBox = poBox;
        this.locatorDesignator = locatorDesignator;
        this.locatorName = locatorName;
        this.cvAddressArea = cvAddressArea;
        this.thoroughfare = thoroughfare;
        this.postName = postName;
        this.adminunitFirstLine = adminunitFirstLine;
        this.adminunitSecondLine = adminunitSecondLine;
        this.postCode = postCode;
    }

    public String getPoBox() {
        return poBox;
    }

    public String getLocatorDesignator() {
        return locatorDesignator;
    }

    public String getLocatorName() {
        return locatorName;
    }

    public String getCvAddressArea() {
        return cvAddressArea;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public String getPostName() {
        return postName;
    }

    public String getAdminunitFirstLine() {
        return adminunitFirstLine;
    }

    public String getAdminunitSecondLine() {
        return adminunitSecondLine;
    }

    public String getPostCode() {
        return postCode;
    }

    public String toBase64EncodedSaml() {
        String addressAsSamlString = getFieldAsSaml(poBox, "PoBox") +
                getFieldAsSaml(locatorDesignator, "LocatorDesignator") +
                getFieldAsSaml(locatorName, "LocatorName") +
                getFieldAsSaml(cvAddressArea, "CvaddressArea") +
                getFieldAsSaml(thoroughfare, "Thoroughfare") +
                getFieldAsSaml(postName, "PostName") +
                getFieldAsSaml(adminunitFirstLine, "AdminunitFirstline") +
                getFieldAsSaml(adminunitSecondLine, "AdminunitSecondline") +
                getFieldAsSaml(postCode, "PostCode");

        return Base64.encodeAsString(addressAsSamlString);
    }

    private String getFieldAsSaml(String value, String samlTag) {
        if(value != null && !value.isEmpty()) {
            return MessageFormat.format("<eidas:{0}>{1}</eidas:{0}>", samlTag, value);
        }
        return "";
    }
}
