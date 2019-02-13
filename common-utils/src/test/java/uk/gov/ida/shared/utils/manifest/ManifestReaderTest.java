package uk.gov.ida.shared.utils.manifest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ManifestReaderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ManifestReader manifestReader = new ManifestReader();

    @Test
    public void shouldReadAttributeValueForAClassFromAJarFile() throws IOException {
        String implementationVersion = manifestReader.getAttributeValueFor(Test.class, "Implementation-Version");

        assertThat(implementationVersion).isNotEmpty();
    }

    @Test
    public void shouldThrowExceptionWhenManifestFileDoesNotExist() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Manifest file not found for the given class.");

        manifestReader.getAttributeValueFor(manifestReader.getClass(), "any-attribute-name");
    }

    @Test
    public void shouldThrowExceptionWhenAttributeDoesNotExist() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Unknown attribute name");

        manifestReader.getAttributeValueFor(Test.class, "some-unknown-attribute");
    }
}