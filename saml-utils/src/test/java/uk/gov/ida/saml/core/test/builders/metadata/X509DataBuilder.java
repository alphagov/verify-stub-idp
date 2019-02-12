package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;

import java.util.ArrayList;
import java.util.List;

public class X509DataBuilder {

    private List<X509Certificate> x509Certificates = new ArrayList<>();

    public static X509DataBuilder aX509Data() {
        return new X509DataBuilder();
    }

    public X509Data build() {
        X509Data x509Data = new org.opensaml.xmlsec.signature.impl.X509DataBuilder().buildObject();

        List<X509Certificate> certificateList = x509Data.getX509Certificates();
        x509Certificates.forEach(certificateList::add);

        return x509Data;
    }

    public X509DataBuilder withX509Certificates(List<X509Certificate> x509Certificate) {
        this.x509Certificates.addAll(x509Certificate);
        return this;
    }

    public X509DataBuilder withX509Certificate(X509Certificate x509Certificate) {
        this.x509Certificates.add(x509Certificate);
        return this;
    }
}
