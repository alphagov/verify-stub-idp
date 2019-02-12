package uk.gov.ida.saml.core.extensions.impl;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.UPRN;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddressImpl extends AbstractSAMLObject implements Address {
    private List<Line> lines = new ArrayList<>();
    private PostCode postCode;
    private DateTime from;
    private DateTime to;
    private InternationalPostCode internationalPostCode;
    private UPRN uprn;
    private boolean verified;

    protected AddressImpl(String namespaceURI, String localName, String namespacePrefix) {
        this(namespaceURI, localName, namespacePrefix, Address.TYPE_NAME);
    }

    protected AddressImpl(String namespaceURI, String localName, String namespacePrefix, QName typeName) {
        super(namespaceURI, localName, namespacePrefix);
        super.setSchemaType(typeName);
    }

    @Override
    public DateTime getFrom() {
        return from;
    }

    @Override
    public void setFrom(DateTime from) {
        this.from = prepareForAssignment(this.from, from);
    }

    @Override
    public DateTime getTo() {
        return to;
    }

    @Override
    public void setTo(DateTime to) {
        this.to = prepareForAssignment(this.to, to);
    }

    @Override
    public boolean getVerified() {
        return verified;
    }

    @Override
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public List<Line> getLines() {
        return lines;
    }

    @Override
    public PostCode getPostCode() {
        return postCode;
    }

    @Override
    public void setPostCode(PostCode postCode) {
        this.postCode = postCode;
    }

    @Override
    public InternationalPostCode getInternationalPostCode() {
        return internationalPostCode;
    }

    @Override
    public void setInternationalPostCode(InternationalPostCode internationalPostCode) {
        this.internationalPostCode = internationalPostCode;
    }

    @Override
    public UPRN getUPRN() {
        return uprn;
    }

    @Override
    public void setUPRN(UPRN uprn) {
        this.uprn = uprn;
    }

    @Override
    public List<XMLObject> getOrderedChildren() {
        List<XMLObject> children = new ArrayList<>();

        children.addAll(lines);
        if (postCode != null) {
            children.add(postCode);
        }
        if (internationalPostCode != null) {
            children.add(internationalPostCode);
        }
        if (uprn != null) {
            children.add(uprn);
        }

        return Collections.unmodifiableList(children);
    }
}
