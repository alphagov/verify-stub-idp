package uk.gov.ida.saml.security.saml.builders;

import org.joda.time.DateTime;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.saml.security.saml.TestSamlObjectFactory;

import java.util.Arrays;

public class SubjectBuilder {

    private static TestSamlObjectFactory testSamlObjectFactory = new TestSamlObjectFactory();
    public static final int NOT_ON_OR_AFTER_DEFAULT_PERIOD = 15;
    public static SubjectBuilder aSubject() {
        return new SubjectBuilder();
    }

    public Subject build() {
        Subject subject = testSamlObjectFactory.createSubject();

        subject.setNameID(buildNameID());
        subject.getSubjectConfirmations().addAll(Arrays.asList(buildSubjectConfirmation()));

        return subject;
    }

    private SubjectConfirmation buildSubjectConfirmation() {
        String method = SubjectConfirmation.METHOD_BEARER;

        SubjectConfirmation subjectConfirmation
                = testSamlObjectFactory.createSubjectConfirmation();
        subjectConfirmation.setMethod(method);

        SubjectConfirmationData subjectConfirmationData
                = buildSubjectConfirmationData();
        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        return subjectConfirmation;
    }


    private NameID buildNameID() {
        NameID nameId = testSamlObjectFactory.createNameId(null);
        nameId.setFormat(null);

        nameId.setFormat(NameIDType.PERSISTENT);

        return nameId;
    }

    private SubjectConfirmationData buildSubjectConfirmationData() {
        SubjectConfirmationData subjectConfirmationData = testSamlObjectFactory.createSubjectConfirmationData();
        subjectConfirmationData.setRecipient(TestEntityIds.HUB_ENTITY_ID);
        subjectConfirmationData.setNotOnOrAfter(DateTime.now().plusMinutes(NOT_ON_OR_AFTER_DEFAULT_PERIOD));
        subjectConfirmationData.setInResponseTo(ResponseBuilder.DEFAULT_REQUEST_ID);
        return subjectConfirmationData;
    }






}
