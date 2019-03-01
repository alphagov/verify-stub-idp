package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

public class SubjectConfirmationBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> method = Optional.ofNullable(SubjectConfirmation.METHOD_BEARER);
    private Optional<SubjectConfirmationData> subjectConfirmationData = Optional.ofNullable(SubjectConfirmationDataBuilder.aSubjectConfirmationData().build());

    public static SubjectConfirmationBuilder aSubjectConfirmation() {
        return new SubjectConfirmationBuilder();
    }

    public SubjectConfirmation build() {
        SubjectConfirmation subjectConfirmation = openSamlXmlObjectFactory.createSubjectConfirmation();

        method.ifPresent(subjectConfirmation::setMethod);
        subjectConfirmationData.ifPresent(subjectConfirmation::setSubjectConfirmationData);

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
