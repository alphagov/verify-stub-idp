package uk.gov.ida.saml.core.test.builders.metadata;

import com.google.common.base.Preconditions;
import org.opensaml.saml.saml2.metadata.Company;

public class CompanyBuilder {
    private String name = "Slate Rock and Gravel Company";

    public static CompanyBuilder aCompany(){
        return new CompanyBuilder();
    }

    public Company build() {
        Company company = new org.opensaml.saml.saml2.metadata.impl.CompanyBuilder().buildObject();
        company.setName(name);
        return company;
    }

    public CompanyBuilder withName(String companyName) {
        Preconditions.checkNotNull(companyName);
        this.name = companyName;
        return this;
    }
}
