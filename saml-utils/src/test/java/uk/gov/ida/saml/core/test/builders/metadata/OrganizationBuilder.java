package uk.gov.ida.saml.core.test.builders.metadata;

import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.impl.OrganizationNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationURLBuilder;

import java.util.Optional;

public class OrganizationBuilder {
    private static final String DEFAULT_LANGUAGE = "en-GB";

    private Optional<OrganizationDisplayName> organizationDisplayName = Optional.ofNullable(OrganizationDisplayNameBuilder.anOrganizationDisplayName().build());
    private Optional<OrganizationName> name = Optional.ofNullable(createName("org-name"));

    private Optional<OrganizationURL> url = Optional.ofNullable(createUrl("http://org"));

    public static OrganizationBuilder anOrganization() {
        return new OrganizationBuilder();
    }

    public Organization build() {
        Organization organization = new org.opensaml.saml.saml2.metadata.impl.OrganizationBuilder().buildObject();

        if (organizationDisplayName.isPresent()) {
            organization.getDisplayNames().add(organizationDisplayName.get());
        }
        if (name.isPresent()){
            organization.getOrganizationNames().add(name.get());
        }
        if (url.isPresent()){
            organization.getURLs().add(url.get());
        }

        return organization;
    }

    private OrganizationName createName(String name) {
        OrganizationName organizationName = new OrganizationNameBuilder().buildObject();
        organizationName.setValue(name);
        organizationName.setXMLLang(DEFAULT_LANGUAGE);
        return organizationName;

    }

    private OrganizationURL createUrl(String url) { 
        OrganizationURL buildObject = new OrganizationURLBuilder().buildObject();
        buildObject.setValue(url);
        buildObject.setXMLLang(DEFAULT_LANGUAGE);
        return buildObject;
    }

    public OrganizationBuilder withDisplayName(OrganizationDisplayName organizationDisplayName) {
        this.organizationDisplayName = Optional.ofNullable(organizationDisplayName);
        return this;
    }

    public OrganizationBuilder withName(String name) {
        this.name = Optional.ofNullable(createName(name));
        return this;
    }

    public OrganizationBuilder withUrl(String url) {
        this.url = Optional.ofNullable(createUrl(url));
        return this;
    }
}
