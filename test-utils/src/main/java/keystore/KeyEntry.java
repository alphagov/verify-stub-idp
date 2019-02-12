package keystore;

public class KeyEntry {
    private final String alias;
    private final String key;

    public String[] getCertificates() {
        return certificates;
    }

    public String getAlias() {
        return alias;
    }

    public String getKey() {
        return key;
    }

    private final String[] certificates;

    public KeyEntry(String alias, String key, String... certificates) {
        this.alias = alias;
        this.key = key;
        this.certificates = certificates;
    }
}
