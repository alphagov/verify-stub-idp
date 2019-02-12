package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.DateOfBirth;

import javax.annotation.Nullable;
import java.util.List;

public class DateOfBirthImpl extends AbstractSAMLObject implements DateOfBirth {

    public static final DateTimeFormatter DATE_OF_BIRTH_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    /** String to hold the date of birth. */
    private LocalDate dateOfBirth;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected DateOfBirthImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /** {@inheritDoc} */
    public void setDateOfBirth(LocalDate dob) {
        dateOfBirth = prepareForAssignment(dateOfBirth, dob);
    }

    @Nullable
    @Override
    public List<XMLObject> getOrderedChildren() {
        return null;
    }
}
