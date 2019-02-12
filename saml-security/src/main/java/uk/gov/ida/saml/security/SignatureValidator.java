package uk.gov.ida.saml.security;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Criterion;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.List;

public abstract class SignatureValidator {
    private final SAMLSignatureProfileValidator samlSignatureProfileValidator = new SAMLSignatureProfileValidator();

    public final boolean validate(SignableSAMLObject signableSAMLObject, String entityId, QName role) throws SecurityException, SignatureException {
        Signature signature = signableSAMLObject.getSignature();

        if (signature == null) {
            throw new SignatureException("Signature in signableSAMLObject is null");
        }

        samlSignatureProfileValidator.validate(signature);

        List<Criterion> additionalCriteria = getAdditionalCriteria(entityId, role);
        CriteriaSet criteria = new CriteriaSet();

        SignatureValidationParameters signatureValidationParameters = new SignatureValidationParameters();
        signatureValidationParameters.setWhitelistedAlgorithms(Arrays.asList(
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512,

                SignatureConstants.ALGO_ID_DIGEST_SHA1,
                SignatureConstants.ALGO_ID_DIGEST_SHA256,
                SignatureConstants.ALGO_ID_DIGEST_SHA512,

                XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1,
                XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256
        ));
        criteria.add(new SignatureValidationParametersCriterion(signatureValidationParameters));

        criteria.addAll(additionalCriteria);

        return getTrustEngine(entityId).validate(signableSAMLObject.getSignature(), criteria);
    }

    protected abstract TrustEngine<Signature> getTrustEngine(String entityId);

    protected abstract List<Criterion> getAdditionalCriteria(String entityId, QName role);
}
