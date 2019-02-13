package uk.gov.ida.shared.utils.manifest;

import java.io.IOException;
import java.net.URL;
import java.util.jar.Manifest;

public class ManifestReader {

    private static final String MANIFEST_FILE_LOCATION = "/META-INF/MANIFEST.MF";

    public String getAttributeValueFor(Class classInJar, String attributeName) throws IOException {
        String attributeValue = getManifestFor(classInJar).getMainAttributes().getValue(attributeName);

        if (attributeValue == null || attributeValue.isEmpty()) {
            throw new IOException("Unknown attribute name");
        }

        return attributeValue;
    }

    private Manifest getManifestFor(Class clazz) throws IOException {
        String manifestFilePath = getManifestFilePath(clazz);

        return new Manifest(new URL(manifestFilePath).openStream());
    }

    private String getManifestFilePath(Class clazz) throws IOException {
        String simpleName = clazz.getSimpleName() + ".class";
        String pathToClass = clazz.getResource(simpleName).toString();
        String pathToJar = pathToClass.substring(0, pathToClass.lastIndexOf("!") + 1);

        if (pathToJar.isEmpty()) {
            throw new IOException("Manifest file not found for the given class.");
        }

        return pathToJar + MANIFEST_FILE_LOCATION;
    }
}
