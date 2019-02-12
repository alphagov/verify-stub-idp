package uk.gov.ida.saml.core.extensions.eidas.impl;

import org.opensaml.saml.common.AbstractSAMLObject;
import uk.gov.ida.saml.core.extensions.eidas.TransliterableString;

public abstract class AbstractTransliterableString extends AbstractSAMLObject implements TransliterableString {

    private Boolean isLatinScript = true;

    protected AbstractTransliterableString(String namespaceURI, String elementLocalName, String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    @Override
    public Boolean isLatinScript() {
        return isLatinScript;
    }

    @Override
    public void setIsLatinScript(Boolean isLatinScript) {
        this.isLatinScript = prepareForAssignment(this.isLatinScript, isLatinScript);
    }
}
