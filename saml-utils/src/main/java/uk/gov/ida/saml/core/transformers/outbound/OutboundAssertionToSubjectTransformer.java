package uk.gov.ida.saml.core.transformers.outbound;

import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.AssertionRestrictions;
import uk.gov.ida.saml.core.domain.OutboundAssertion;

import javax.inject.Inject;

public class OutboundAssertionToSubjectTransformer {
    private OpenSamlXmlObjectFactory openSamlXmlObjectFactory;

    @Inject
    public OutboundAssertionToSubjectTransformer(OpenSamlXmlObjectFactory openSamlXmlObjectFactory) {
        this.openSamlXmlObjectFactory = openSamlXmlObjectFactory;
    }

    public Subject transform(OutboundAssertion originalAssertion) {
        Subject subject = openSamlXmlObjectFactory.createSubject();
        NameID nameId = openSamlXmlObjectFactory.createNameId(originalAssertion.getPersistentId().getNameId());

        nameId.setFormat(NameIDType.PERSISTENT);
        subject.setNameID(nameId);

        SubjectConfirmation subjectConfirmation = openSamlXmlObjectFactory.createSubjectConfirmation();
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
        SubjectConfirmationData subjectConfirmationData = openSamlXmlObjectFactory.createSubjectConfirmationData();

        final AssertionRestrictions assertionRestrictions = originalAssertion.getAssertionRestrictions();
        subjectConfirmationData.setNotOnOrAfter(assertionRestrictions.getNotOnOrAfter());
        subjectConfirmationData.setInResponseTo(assertionRestrictions.getInResponseTo());
        subjectConfirmationData.setRecipient(assertionRestrictions.getRecipient());
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
        subject.getSubjectConfirmations().add(subjectConfirmation);
        return subject;

    }
}
