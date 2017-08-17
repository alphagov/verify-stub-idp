package uk.gov.ida.saml.idp.test.builders;

import com.google.common.base.Optional;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

import static com.google.common.base.Optional.fromNullable;

public class SubjectConfirmationBuilder {

    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<String> method = fromNullable(SubjectConfirmation.METHOD_BEARER);
    private Optional<SubjectConfirmationData> subjectConfirmationData = fromNullable(SubjectConfirmationDataBuilder.aSubjectConfirmationData().build());

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
        this.method = fromNullable(method);
        return this;
    }

    public SubjectConfirmationBuilder withSubjectConfirmationData(SubjectConfirmationData subjectConfirmationData) {
        this.subjectConfirmationData = fromNullable(subjectConfirmationData);
        return this;
    }
}
