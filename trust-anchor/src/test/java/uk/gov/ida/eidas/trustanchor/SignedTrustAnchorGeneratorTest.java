package uk.gov.ida.eidas.trustanchor;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SignedTrustAnchorGeneratorTest {

    @Test
    public void testSignerThrowsExceptionWhenInputFileNotReadable() {

        PrivateKey key = mock(PrivateKey.class);
        X509Certificate certificate = mock(X509Certificate.class);
        
        File inputFile = mock(File.class);
        when(inputFile.getPath()).thenReturn("test");
        when(inputFile.canRead()).thenReturn(false);
        List<File> inputFiles = ImmutableList.of(inputFile);

        File outputFile = mock(File.class);

        SignedTrustAnchorGenerator signer = new SignedTrustAnchorGenerator(key, certificate, inputFiles, outputFile);

        assertThrows(FileNotFoundException.class, signer::generate);
    }
}
