package uk.gov.ida.saml.security;

import net.shibboleth.utilities.java.support.resolver.Criterion;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetadataBackedSignatureValidator extends SignatureValidator {

    private final ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine;
    private final Optional<CertificateChainEvaluableCriterion> certificateChainEvaluableCriteria;

    public static MetadataBackedSignatureValidator withoutCertificateChainValidation(ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine) {
        return new MetadataBackedSignatureValidator(explicitKeySignatureTrustEngine);
    }

    public static MetadataBackedSignatureValidator withCertificateChainValidation(ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine, CertificateChainEvaluableCriterion certificateChainEvaluableCriterion) {
        return new MetadataBackedSignatureValidator(explicitKeySignatureTrustEngine, certificateChainEvaluableCriterion);
    }

    private MetadataBackedSignatureValidator(ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine) {
        this.explicitKeySignatureTrustEngine = explicitKeySignatureTrustEngine;
        this.certificateChainEvaluableCriteria = Optional.empty();
    }

    private MetadataBackedSignatureValidator(ExplicitKeySignatureTrustEngine explicitKeySignatureTrustEngine, CertificateChainEvaluableCriterion certificateChainEvaluableCriterion) {
        this.explicitKeySignatureTrustEngine = explicitKeySignatureTrustEngine;
        this.certificateChainEvaluableCriteria = Optional.of(certificateChainEvaluableCriterion);
    }

    @Override
    protected List<Criterion> getAdditionalCriteria(String entityId, QName role) {
        List<Criterion> criteriaSet = new ArrayList<>();
        criteriaSet.add(new EntityIdCriterion(entityId));
        criteriaSet.add(new EntityRoleCriterion(role));
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        this.certificateChainEvaluableCriteria.map(criteriaSet::add);

        return criteriaSet;
    }

    @Override
    protected TrustEngine<Signature> getTrustEngine(String entityId) {
        return explicitKeySignatureTrustEngine;
    }
}
