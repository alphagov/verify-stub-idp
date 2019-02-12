package uk.gov.ida.saml.core;

import org.apache.xml.security.signature.XMLSignature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.Initializer;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicSignatureValidationConfiguration;

import java.security.Security;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EidasSecurityConfigurationInitializer implements Initializer {
    @Override
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
        BasicSignatureValidationConfiguration signatureValidationConfiguration = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureValidationConfiguration();
        Collection<String> defaultAlgos = signatureValidationConfiguration.getWhitelistedAlgorithms();
        Collection<String> customAlgos = Arrays.asList(
                XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256_MGF1
        );
        signatureValidationConfiguration.setWhitelistedAlgorithms(Stream.concat(defaultAlgos.stream(), customAlgos.stream()).collect(Collectors.toList()));
        ConfigurationService.register(SignatureValidationConfiguration.class, signatureValidationConfiguration);
    }
}
