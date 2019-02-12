package uk.gov.ida.saml.serializers;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.apache.commons.codec.binary.StringUtils;
import org.opensaml.core.xml.XMLObject;
import org.w3c.dom.Element;

import java.util.function.Function;

public class XmlObjectToBase64EncodedStringTransformer<TInput extends XMLObject> implements Function<TInput,String> {

    @Override
    public String apply(XMLObject signableXMLObject) {
        Element signedElement = marshallToElement(signableXMLObject);
        String node = SerializeSupport.nodeToString(signedElement);
        return Base64Support.encode(StringUtils.getBytesUtf8(node), Base64Support.UNCHUNKED);
    }

    private static Element marshallToElement(XMLObject rootObject) {
        return new XmlObjectToElementTransformer<>().apply(rootObject);
    }

}
