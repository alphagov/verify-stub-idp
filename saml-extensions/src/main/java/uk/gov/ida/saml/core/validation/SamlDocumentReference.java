package uk.gov.ida.saml.core.validation;

public final class SamlDocumentReference {
    public static SamlDocumentReference idaAttributes11a(String documentSection) { return new SamlDocumentReference("Identity Assurance Hub Service Profile - SAML Attributes v1.1a", documentSection); }
    public static SamlDocumentReference samlCore20(String documentSection) { return new SamlDocumentReference("Saml Core 2.0", documentSection); }
    public static SamlDocumentReference samlProfiles20(String documentSection) { return new SamlDocumentReference("Saml Profiles 2.0", documentSection); }
    public static SamlDocumentReference hubProfile11a(String documentSection) { return new SamlDocumentReference("Hub Service Profile 1.1a", documentSection); }
    public static SamlDocumentReference samlBindings(String documentSection) { return new SamlDocumentReference("Saml Bindings 2.0", documentSection); }
    public static SamlDocumentReference unspecified() { return new SamlDocumentReference("Unspecified", "--"); }

    private String documentName;
    private String documentSection;

    private SamlDocumentReference(String documentName, String documentSection) {
        this.documentName = documentName;
        this.documentSection = documentSection;
    }

    @Override
    public String toString() {
        return "DocumentReference{" +
                "documentName='" + documentName + '\'' +
                ", documentSection='" + documentSection + '\'' +
                '}';
    }
}
