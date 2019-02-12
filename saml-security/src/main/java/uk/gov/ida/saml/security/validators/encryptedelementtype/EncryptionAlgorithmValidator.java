package uk.gov.ida.saml.security.validators.encryptedelementtype;

import com.google.common.collect.ImmutableSet;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import uk.gov.ida.saml.core.validation.SamlTransformationErrorException;
import uk.gov.ida.saml.core.validation.SamlValidationSpecificationFailure;
import uk.gov.ida.saml.security.errors.SamlTransformationErrorFactory;

import java.util.Set;

public class EncryptionAlgorithmValidator {
    private final Set<String> algorithmWhitelist;
    private final Set<String> keyTransportAlgorithmWhitelist;

    public EncryptionAlgorithmValidator() {
        this.algorithmWhitelist = ImmutableSet.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        this.keyTransportAlgorithmWhitelist = ImmutableSet.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
    }

    public EncryptionAlgorithmValidator(Set<String> algorithmWhitelist, Set<String> keyTransportAlgorithmWhitelist) {
        this.algorithmWhitelist = algorithmWhitelist;
        this.keyTransportAlgorithmWhitelist = keyTransportAlgorithmWhitelist;
    }

    public void validate(EncryptedElementType encryptedElement) {
        final String algorithm = encryptedElement.getEncryptedData().getEncryptionMethod().getAlgorithm();
        if (!this.algorithmWhitelist.contains(algorithm)) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.unsupportedEncryptionAlgortithm(algorithm);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        EncryptionMethod encryptionMethod;
        if (encryptedElement.getEncryptedKeys().size() != 0) {
            encryptionMethod = encryptedElement.getEncryptedKeys().get(0).getEncryptionMethod();
        } else if (encryptedElement.getEncryptedData().getKeyInfo().getEncryptedKeys().size() != 0) {
            encryptionMethod = encryptedElement.getEncryptedData().getKeyInfo().getEncryptedKeys().get(0).getEncryptionMethod();
        } else {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.unableToLocateEncryptedKey();
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }

        final String keyTransportAlgorithm = encryptionMethod.getAlgorithm();
        if (!keyTransportAlgorithmWhitelist.contains(keyTransportAlgorithm)) {
            SamlValidationSpecificationFailure failure = SamlTransformationErrorFactory.unsupportedKeyEncryptionAlgorithm(keyTransportAlgorithm);
            throw new SamlTransformationErrorException(failure.getErrorMessage(), failure.getLogLevel());
        }
    }
}
