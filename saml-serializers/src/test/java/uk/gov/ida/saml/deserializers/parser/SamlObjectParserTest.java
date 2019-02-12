package uk.gov.ida.saml.deserializers.parser;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.UsageType;
import org.xml.sax.SAXParseException;

public class SamlObjectParserTest {

    @Before
    public void setup() throws InitializationException {
        InitializationService.initialize();
    }

    @Test
    public void shouldNotFailWithEntityDescriptorStuff() throws Exception {
        SamlObjectParser samlObjectParser = new SamlObjectParser();
        EntityDescriptor samlObject = samlObjectParser.getSamlObject(entityDescriptor);
        Assertions.assertThat(samlObject.getIDPSSODescriptor(SAMLConstants.SAML20P_NS).getKeyDescriptors().get(0).getUse()).isEqualTo(UsageType.SIGNING);
    }

    @Test
    public void shouldFailWhenNaughtyXml() {
        String xmlString = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE lolz [\n" +
                " <!ENTITY lol \"lol\">\n" +
                " <!ELEMENT lolz (#PCDATA)>\n" +
                " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n" +
                " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n" +
                " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n" +
                " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n" +
                " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n" +
                " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n" +
                " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n" +
                " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n" +
                " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n" +
                "]>\n" +
                "<lolz>&lol9;</lolz>";

        Assertions.assertThatThrownBy(() -> new SamlObjectParser().getSamlObject(xmlString))
                .hasCauseInstanceOf(SAXParseException.class)
                .isInstanceOf(XMLParserException.class);
    }

    private static final String entityDescriptor = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ext=\"urn:uk:gov:cabinet-office:tc:saml:metadata:extensions\" cacheDuration=\"PT1M40.000S\" entityID=\"http://stub_idp.acme.org/foo-bar-baz/SSO/POST\" validUntil=\"2012-11-14T14:40:08.224Z\" xsi:type=\"md:EntityDescriptorType\"><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><ds:Reference URI=\"\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"><ec:InclusiveNamespaces xmlns:ec=\"http://www.w3.org/2001/10/xml-exc-c14n#\" PrefixList=\"xs\"/></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><ds:DigestValue>ziVutS5Scw/+waR24jfJaTkX9aE=</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>QovUWDK1LAFiZNdgc4j0E07vpYKHJL7/ylL5kdu314wqvZ+yf6UoRXKGUCnzCxU1cN0sz95E7/vG0N+pl/cuAfvSWpTGbgHhWHTlGWoXBFh7Y4bALKANfE/R8lHIfegAPDI8yuOyquIQPqhFgaz1euVREtmCFNxysfy8UsyoW/g=</ds:SignatureValue></ds:Signature><md:IDPSSODescriptor protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\" xsi:type=\"ext:IDPSSODescriptorType\"><md:KeyDescriptor use=\"signing\" xsi:type=\"md:KeyDescriptorType\"><ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" xsi:type=\"ds:KeyInfoType\"><ds:X509Data xsi:type=\"ds:X509DataType\"><ds:X509Certificate xsi:type=\"xs:base64Binary\">MIICsDCCAhmgAwIBAgIJAMaC0hPH6QKpMA0GCSqGSIb3DQEBBQUAMEUxCzAJBgNV\n" +
            "BAYTAkFVMRMwEQYDVQQIEwpTb21lLVN0YXRlMSEwHwYDVQQKExhJbnRlcm5ldCBX\n" +
            "aWRnaXRzIFB0eSBMdGQwHhcNMTIwOTAzMTYzNTAzWhcNMTIxMDAzMTYzNTAzWjBF\n" +
            "MQswCQYDVQQGEwJBVTETMBEGA1UECBMKU29tZS1TdGF0ZTEhMB8GA1UEChMYSW50\n" +
            "ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n" +
            "gQDCkDX0vtaMVIXXe62fUY682ig1pjNrx/uCEW00MzbiGg+t5vS3ofB6biurVXvX\n" +
            "nTW4muZi9/ysvC+0oZzNUDkaE/JDJfArnxBT8P7qZAsHG7W6lZ+5W7fasMXbJayU\n" +
            "BnkS1j44rb3TkvrtG/Zvd3hwVKk66TjR7+nTGslP4hICQQIDAQABo4GnMIGkMB0G\n" +
            "A1UdDgQWBBQaCzDJiJVYujeJC4T9KcBgoZ0E8jB1BgNVHSMEbjBsgBQaCzDJiJVY\n" +
            "ujeJC4T9KcBgoZ0E8qFJpEcwRTELMAkGA1UEBhMCQVUxEzARBgNVBAgTClNvbWUt\n" +
            "U3RhdGUxITAfBgNVBAoTGEludGVybmV0IFdpZGdpdHMgUHR5IEx0ZIIJAMaC0hPH\n" +
            "6QKpMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAHBvSBGdLmksxWRmb\n" +
            "bdcYz8fCKbjCk6TEzIumu9WQjQqJk8o05VmOKLkIY0tGq185OclxTB8740T1MOlG\n" +
            "oFjgWi3cSg/UymzHvSeIx2Hf/3bx9OEQeUBcXqwQ3MHHBJw5eFwuSHdygrCGiDSr\n" +
            "YMMh3ES6s8/b3qPsW4yVh1Z3Usc=</ds:X509Certificate></ds:X509Data></ds:KeyInfo></md:KeyDescriptor><md:SingleSignOnService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\"http://foo.com/bar\" xsi:type=\"md:EndpointType\"/></md:IDPSSODescriptor><md:Organization xsi:type=\"md:OrganizationType\"><md:OrganizationDisplayName xml:lang=\"en-GB\" xsi:type=\"md:localizedNameType\">organizationName</md:OrganizationDisplayName></md:Organization></md:EntityDescriptor>";

}
