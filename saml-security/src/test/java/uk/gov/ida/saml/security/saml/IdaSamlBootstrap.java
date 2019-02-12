package uk.gov.ida.saml.security.saml;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import uk.gov.ida.saml.core.extensions.Address;
import uk.gov.ida.saml.core.extensions.Date;
import uk.gov.ida.saml.core.extensions.Gender;
import uk.gov.ida.saml.core.extensions.Gpg45Status;
import uk.gov.ida.saml.core.extensions.IPAddress;
import uk.gov.ida.saml.core.extensions.IdpFraudEventId;
import uk.gov.ida.saml.core.extensions.InternationalPostCode;
import uk.gov.ida.saml.core.extensions.Line;
import uk.gov.ida.saml.core.extensions.PersonName;
import uk.gov.ida.saml.core.extensions.PostCode;
import uk.gov.ida.saml.core.extensions.StatusValue;
import uk.gov.ida.saml.core.extensions.StringBasedMdsAttributeValue;
import uk.gov.ida.saml.core.extensions.UPRN;
import uk.gov.ida.saml.core.extensions.Verified;
import uk.gov.ida.saml.core.extensions.impl.AddressBuilder;
import uk.gov.ida.saml.core.extensions.impl.AddressMarshaller;
import uk.gov.ida.saml.core.extensions.impl.AddressUnmarshaller;
import uk.gov.ida.saml.core.extensions.impl.DateBuilder;
import uk.gov.ida.saml.core.extensions.impl.DateImpl;
import uk.gov.ida.saml.core.extensions.impl.GenderBuilder;
import uk.gov.ida.saml.core.extensions.impl.GenderImpl;
import uk.gov.ida.saml.core.extensions.impl.Gpg45StatusBuilder;
import uk.gov.ida.saml.core.extensions.impl.Gpg45StatusImpl;
import uk.gov.ida.saml.core.extensions.impl.IPAddressBuilder;
import uk.gov.ida.saml.core.extensions.impl.IPAddressImpl;
import uk.gov.ida.saml.core.extensions.impl.IdpFraudEventIdBuilder;
import uk.gov.ida.saml.core.extensions.impl.IdpFraudEventIdImpl;
import uk.gov.ida.saml.core.extensions.impl.InternationalPostCodeBuilder;
import uk.gov.ida.saml.core.extensions.impl.LineBuilder;
import uk.gov.ida.saml.core.extensions.impl.PersonNameBuilder;
import uk.gov.ida.saml.core.extensions.impl.PersonNameImpl;
import uk.gov.ida.saml.core.extensions.impl.PostCodeBuilder;
import uk.gov.ida.saml.core.extensions.impl.StatusValueBuilder;
import uk.gov.ida.saml.core.extensions.impl.StatusValueImpl;
import uk.gov.ida.saml.core.extensions.impl.StringBasedMdsAttributeValueBuilder;
import uk.gov.ida.saml.core.extensions.impl.StringBasedMdsAttributeValueImpl;
import uk.gov.ida.saml.core.extensions.impl.StringValueSamlObjectImpl;
import uk.gov.ida.saml.core.extensions.impl.UPRNBuilder;
import uk.gov.ida.saml.core.extensions.impl.VerifiedBuilder;
import uk.gov.ida.saml.core.extensions.impl.VerifiedImpl;

public abstract class IdaSamlBootstrap {

    private static boolean hasBeenBootstrapped = false;

    public static class BootstrapException extends RuntimeException{
        public BootstrapException(Exception e){
            super(e);
        }
    }

    public static synchronized void bootstrap() {
        if (hasBeenBootstrapped) {
            return;
        }

        try {
            doBootstrapping();
        } catch (InitializationException e) {
            throw new BootstrapException(e);
        }

        hasBeenBootstrapped = true;
    }

    private static void doBootstrapping() throws InitializationException {
        InitializationService.initialize();

        //HACK: Why is the string type even registered? It can't ever get the element name right in that case, can it? [Mark/Peter 15/1/2013]
        XMLObjectProviderRegistrySupport.deregisterObjectProvider(XSString.TYPE_NAME);

        XMLObjectProviderRegistrySupport.registerObjectProvider(PersonName.TYPE_NAME, new PersonNameBuilder(), PersonNameImpl.MARSHALLER, PersonNameImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Date.TYPE_NAME, new DateBuilder(), DateImpl.MARSHALLER, DateImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Address.TYPE_NAME, new AddressBuilder(), new AddressMarshaller(), new AddressUnmarshaller());
        XMLObjectProviderRegistrySupport.registerObjectProvider(PostCode.DEFAULT_ELEMENT_NAME, new PostCodeBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(InternationalPostCode.DEFAULT_ELEMENT_NAME, new InternationalPostCodeBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(UPRN.DEFAULT_ELEMENT_NAME, new UPRNBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Line.DEFAULT_ELEMENT_NAME, new LineBuilder(), StringValueSamlObjectImpl.MARSHALLER, StringValueSamlObjectImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StringBasedMdsAttributeValue.TYPE_NAME, new StringBasedMdsAttributeValueBuilder(), StringBasedMdsAttributeValueImpl.MARSHALLER, StringBasedMdsAttributeValueImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StringBasedMdsAttributeValue.DEFAULT_ELEMENT_NAME, new StringBasedMdsAttributeValueBuilder(), StringBasedMdsAttributeValueImpl.MARSHALLER, StringBasedMdsAttributeValueImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Gender.TYPE_NAME, new GenderBuilder(), GenderImpl.MARSHALLER, GenderImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(IdpFraudEventId.TYPE_NAME, new IdpFraudEventIdBuilder(), IdpFraudEventIdImpl.MARSHALLER, IdpFraudEventIdImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Gpg45Status.TYPE_NAME, new Gpg45StatusBuilder(), Gpg45StatusImpl.MARSHALLER, IdpFraudEventIdImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(IPAddress.TYPE_NAME, new IPAddressBuilder(), IPAddressImpl.MARSHALLER, IPAddressImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(Verified.TYPE_NAME, new VerifiedBuilder(), VerifiedImpl.MARSHALLER, VerifiedImpl.UNMARSHALLER);
        XMLObjectProviderRegistrySupport.registerObjectProvider(StatusValue.DEFAULT_ELEMENT_NAME, new StatusValueBuilder(), StatusValueImpl.MARSHALLER, StatusValueImpl.UNMARSHALLER);
    }
}
