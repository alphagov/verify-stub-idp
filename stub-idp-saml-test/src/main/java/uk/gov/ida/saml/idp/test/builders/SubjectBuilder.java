package uk.gov.ida.saml.idp.test.builders;

import java.util.Optional;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.ida.saml.idp.test.builders.NameIdBuilder.aNameId;

public class SubjectBuilder {

    private static OpenSamlXmlObjectFactory openSamlXmlObjectFactory = new OpenSamlXmlObjectFactory();
    private Optional<NameID> nameIdValue = Optional.ofNullable(aNameId().build());
    private List<SubjectConfirmation> subjectConfirmations = new ArrayList<>();
    private boolean shouldAddDefaultSubjectConfirmation = true;

    public static SubjectBuilder aSubject() {
        return new SubjectBuilder();
    }

    public Subject build() {
        Subject subject = openSamlXmlObjectFactory.createSubject();

        nameIdValue.ifPresent(subject::setNameID);

        if (shouldAddDefaultSubjectConfirmation) {
            subjectConfirmations.add(SubjectConfirmationBuilder.aSubjectConfirmation().build());
        }
        subject.getSubjectConfirmations().addAll(subjectConfirmations);

        return subject;
    }

    public SubjectBuilder withNameId(NameID nameId) {
        this.nameIdValue = Optional.ofNullable(nameId);
        return this;
    }

    public SubjectBuilder withSubjectConfirmation(SubjectConfirmation subjectConfirmation) {
        this.subjectConfirmations.add(subjectConfirmation);
        this.shouldAddDefaultSubjectConfirmation = false;
        return this;
    }

    public SubjectBuilder withPersistentId(String persistentId) {
        return withNameId(aNameId().withValue(persistentId).build());
    }
}
