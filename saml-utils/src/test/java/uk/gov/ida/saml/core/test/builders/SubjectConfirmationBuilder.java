package uk.gov.ida.saml.core.test.builders;

import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.test.OpenSamlXmlObjectFactory;

import java.util.Optional;

public class SubjectConfirmationBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> method = Optional.of(SubjectConfirmation.METHOD_BEARER);
    private Optional<SubjectConfirmationData> subjectConfirmationData = Optional.of(SubjectConfirmationDataBuilder.aSubjectConfirmationData().build());

    public static SubjectConfirmationBuilder aSubjectConfirmation() {
        return new SubjectConfirmationBuilder();
    }

    public SubjectConfirmation build() {
        SubjectConfirmation subjectConfirmation = openSamlXmlObjectFactory.createSubjectConfirmation();

        if (method.isPresent()) {
            subjectConfirmation.setMethod(method.get());
        }

        if (subjectConfirmationData.isPresent()) {
            subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData.get());
        }

        return subjectConfirmation;
    }

    public SubjectConfirmationBuilder withMethod(String method) {
        this.method = Optional.ofNullable(method);
        return this;
    }

    public SubjectConfirmationBuilder withSubjectConfirmationData(SubjectConfirmationData subjectConfirmationData) {
        this.subjectConfirmationData = Optional.ofNullable(subjectConfirmationData);
        return this;
    }
}
