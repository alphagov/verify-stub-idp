package uk.gov.ida.eidas.metadata;

import org.opensaml.saml.common.SignableSAMLObject;
import uk.gov.ida.eidas.metadata.saml.SamlObjectMarshaller;
import uk.gov.ida.eidas.utils.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

public class SignedMetadataGenerator {

    private PrivateKey key;
    private X509Certificate certificate;
    private AlgorithmType algorithm;
    private File inputFile;
    private File outputFile;

    public SignedMetadataGenerator(PrivateKey key, X509Certificate certificate, AlgorithmType algorithm, File inputFile, File outputFile) {
        this.key = key;
        this.certificate = certificate;
        this.algorithm = algorithm;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public Void generate() throws Exception {
        if (!inputFile.canRead()) {
            throw new FileNotFoundException("Could not read file: " + inputFile.getPath());
        }

        if (outputFile != null && !(outputFile.canWrite() || (!outputFile.exists() && outputFile.getAbsoluteFile().getParentFile().canWrite()))) {
            throw new FileNotFoundException("Cannot write to output file: " + outputFile.getAbsolutePath());
        }

        String metadataString = FileReader.readFileContent(inputFile);

        SignableSAMLObject signedMetadataObject = new ConnectorMetadataSigner(certificate, key, algorithm).sign(metadataString);

        boolean valid = new MetadataSignatureValidator(certificate.getPublicKey(), key).validate(signedMetadataObject);
        if(!valid) throw new SignatureException("Unable to sign Connector Metadata");

        String signedMetadata = new SamlObjectMarshaller().transformToString(signedMetadataObject);

        final OutputStreamWriter output = (outputFile == null ? new OutputStreamWriter(System.out) : new FileWriter(outputFile));
        output.write(signedMetadata);
        output.close();

        return null;
    }
}
