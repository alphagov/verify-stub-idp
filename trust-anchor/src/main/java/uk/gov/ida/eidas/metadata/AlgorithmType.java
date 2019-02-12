package uk.gov.ida.eidas.metadata;

import org.apache.xml.security.signature.XMLSignature;

public enum AlgorithmType {
    RSA(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256),
    ECDSA(XMLSignature.ALGO_ID_SIGNATURE_ECDSA_SHA256);

    private String algorithmUri;

    AlgorithmType(String algorithmUri) {
        this.algorithmUri = algorithmUri;
    }

    public String getAlgorithmURI(){
        return algorithmUri;
    }
}
