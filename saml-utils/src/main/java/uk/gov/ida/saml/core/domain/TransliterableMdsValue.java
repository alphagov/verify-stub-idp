package uk.gov.ida.saml.core.domain;

public class TransliterableMdsValue extends SimpleMdsValue<String> {
    private String nonLatinScriptValue;

    public TransliterableMdsValue(String value, String nonLatinScriptValue) {
        super(value, null, null, true);
        this.nonLatinScriptValue = nonLatinScriptValue;
    }

    public TransliterableMdsValue(SimpleMdsValue<String> simpleMdsValue) {
        super(simpleMdsValue.getValue(), simpleMdsValue.getFrom(), simpleMdsValue.getTo(), simpleMdsValue.isVerified());
        this.nonLatinScriptValue = null;
    }

    public String getNonLatinScriptValue() {
        return nonLatinScriptValue;
    }
}
