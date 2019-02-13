package uk.gov.ida.shared.utils.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class FileUtils {

    private static final int BUFFER_SIZE = 4096;

    public static byte[] readStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            ous.write(buffer, 0, read);
        }
        return ous.toByteArray();
    }

}
