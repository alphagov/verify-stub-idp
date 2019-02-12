package uk.gov.ida.saml.serializers;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.w3c.dom.Element;

import java.util.function.Function;


public class XmlObjectToElementTransformer<TInput extends XMLObject> implements Function<TInput,Element> {

    public Element apply(TInput rootObject) {
        try {
            return XMLObjectSupport.marshall(rootObject);
        } catch (MarshallingException e) {
            throw new RuntimeException(e);
        }
    }
}
