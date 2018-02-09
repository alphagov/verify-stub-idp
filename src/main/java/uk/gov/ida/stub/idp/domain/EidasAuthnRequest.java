package uk.gov.ida.stub.idp.domain;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.AuthnRequest;
import uk.gov.ida.saml.core.extensions.RequestedAttribute;
import uk.gov.ida.saml.core.extensions.impl.RequestedAttributesImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        return new EidasAuthnRequest(requestId, issuer, destination, requestLoa, getRequestAttributes(request));
    }

    private static List<RequestedAttribute> getRequestAttributes(AuthnRequest request){

        Optional<XMLObject> requestedAttributesObj = request.getExtensions().getOrderedChildren()
                .stream()
                .filter(x -> x.getClass() == RequestedAttributesImpl.class)
                .findFirst();

        return requestedAttributesObj
                .map(XMLObject::getOrderedChildren)
                .orElse(Collections.emptyList())
                .stream()
                .map(xmlObject -> (RequestedAttribute) xmlObject)
                .collect(Collectors.toList());
    }
}
