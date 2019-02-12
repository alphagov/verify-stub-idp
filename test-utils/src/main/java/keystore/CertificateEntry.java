package keystore;

public class CertificateEntry {
    private final String alias;
    private final String certificate;

    public CertificateEntry(String alias, String certificate) {
        this.alias = alias;
        this.certificate = certificate;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getAlias() {
        return alias;
    }
}
