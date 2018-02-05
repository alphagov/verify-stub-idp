package uk.gov.ida.stub.idp.domain;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesImpl;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EidasAuthnRequest {

    private final String requestId;
    private final String issuer;
    private final String destination;
    private final String requestedLoa;
    private final List<RequestedAttribute> attributes;

    public EidasAuthnRequest(String requestId, String issuer, String destination, String requestedLoa, List<RequestedAttribute> attributes) {
        this.requestId = requestId;
        this.issuer = issuer;
        this.destination = destination;
        this.requestedLoa = requestedLoa;
        this.attributes = attributes;
    }

    public List<RequestedAttribute> getAttributes() {
        return attributes;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getDestination() {
        return destination;
    }

    public String getRequestedLoa() {
        return requestedLoa;
    }

    public static EidasAuthnRequest buildFromAuthnRequest(AuthnRequest request) {
        String requestId = request.getID();
        String issuer = request.getIssuer().getValue();
        String destination = request.getDestination();
        String requestLoa = request.getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getAuthnContextClassRef();

        return new EidasAuthnRequest(requestId, issuer, destination, requestLoa, getRequestAttributeList(request));
    }

    private static List<RequestedAttribute> getRequestAttributeList(AuthnRequest request){
        List<RequestedAttribute> attributeList = new ArrayList<>();

        Optional<XMLObject> requestedAttributesObj = request.getExtensions().getOrderedChildren()
                .stream()
                .filter(x -> x.getClass() == RequestedAttributesImpl.class)
                .findFirst();

        List<XMLObject> requestedAttributes = requestedAttributesObj
                .map(obj -> (obj).getOrderedChildren())
                .orElse(Collections.emptyList());

        for (XMLObject object : requestedAttributes){
            RequestedAttribute requestedAttribute = build(RequestedAttribute.DEFAULT_ELEMENT_NAME);
            requestedAttribute.getAttributeValues().add(object);
            attributeList.add(requestedAttribute);
        }

        return attributeList;
    }

    private static <T extends XMLObject> T build(QName elementName) {
        return (T) XMLObjectSupport.buildXMLObject(elementName);
    }
}
