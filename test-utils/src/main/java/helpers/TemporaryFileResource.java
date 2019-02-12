package helpers;

import com.google.common.base.Throwables;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TemporaryFileResource implements ManagedFileResource {
    private final File tempFile;
    private final byte[] content;

    public File getTempFile() {
        return tempFile;
    }

    public String getPath() {
        return tempFile.getAbsolutePath();
    }


    public TemporaryFileResource(File tempFile, byte[] content) {
        this.tempFile = tempFile;
        this.content = content;
    }

    @Override
    public void create() {
        try {
            FileUtils.writeByteArrayToFile(tempFile, content);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void delete() {
      tempFile.delete();
    }
}
