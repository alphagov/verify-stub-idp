package uk.gov.ida.saml.core.extensions.eidas;

import org.opensaml.saml.saml2.core.AttributeValue;

public interface TransliterableString extends AttributeValue {
    String IS_LATIN_SCRIPT_ATTRIBUTE_NAME = "LatinScript";

    Boolean isLatinScript();

    void setIsLatinScript(Boolean latinScript);

}
