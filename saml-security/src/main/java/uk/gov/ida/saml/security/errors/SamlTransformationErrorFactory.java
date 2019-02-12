package uk.gov.ida.saml.security.errors;

import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.core.validation.errors.GenericHubProfileValidationSpecification;
import uk.gov.ida.saml.core.validation.errors.SamlValidationSpecification;

public final class SamlTransformationErrorFactory {

    private SamlTransformationErrorFactory() {
    }

    public static SamlValidationSpecificationFailure unableToDeserializeStringToOpenSaml(final String message) {
        return new SamlValidationSpecification(SamlValidationSpecification.DESERIALIZATION_ERROR, message);
    }

    public static SamlValidationSpecificationFailure emptyIssuer() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.EMPTY_ISSUER);

    }

    public static SamlValidationSpecificationFailure illegalIssuerFormat(final String providedFormat, final String expectedFormat) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.ILLEGAL_ISSUER_FORMAT, providedFormat, expectedFormat);
    }

    public static SamlValidationSpecificationFailure unableToDecrypt(final String message) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.UNABLE_TO_DECRYPT, message);
    }

    public static SamlValidationSpecificationFailure unsupportedSignatureEncryptionAlgortithm(final String algorithm) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.UNSUPPORTED_SIGNATURE_ENCRYPTION_ALGORITHM, algorithm);
    }

    public static SamlValidationSpecificationFailure unsupportedEncryptionAlgortithm(final String algorithm) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.ENCRYPTION_ALGORITHM_SHOULD_BE_AES128, algorithm);
    }

    public static SamlValidationSpecificationFailure unableToLocateEncryptedKey() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.UNABLE_TO_LOCATE_ENCRYPTED_KEY);
    }

    public static SamlValidationSpecificationFailure unsupportedKeyEncryptionAlgorithm(final String algorithm) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.KEY_ENCRYPTION_ALGORITHM_SHOULD_BE_RSAOAEP, algorithm);
    }

    public static SamlValidationSpecificationFailure missingSignature() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.MISSING_SIGNATURE);
    }

    public static SamlValidationSpecificationFailure signatureNotSigned() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.SIGNATURE_NOT_SIGNED);
    }

    public static SamlValidationSpecificationFailure missingIssuer() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.MISSING_ISSUER);
    }

    public static SamlValidationSpecificationFailure invalidSignatureForAssertion(final String assertionId) {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.INVALID_ASSERTION_SIGNATURE, assertionId);
    }

    public static SamlValidationSpecificationFailure invalidMessageSignature() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.INVALID_MESSAGE_SIGNATURE);
    }

    public static SamlValidationSpecificationFailure unableToValidateMessageSignature() {
        return new GenericHubProfileValidationSpecification(GenericHubProfileValidationSpecification.UNABLE_TO_VALIDATE_MESSAGE_SIGNATURE);
    }
}
