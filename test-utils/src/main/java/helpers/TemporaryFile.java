package helpers;

import org.junit.rules.ExternalResource;

import java.io.File;

public class TemporaryFile extends ExternalResource {
    private final TemporaryFileResource temporaryFileResource;

    public TemporaryFile(TemporaryFileResource temporaryFileResource) {

        this.temporaryFileResource = temporaryFileResource;
    }

    @Override
    protected void before() throws Throwable {
       temporaryFileResource.create();
    }

    @Override
    protected void after() {
        temporaryFileResource.delete();
    }

    public String getPath() {
        return temporaryFileResource.getPath();
    }

    public File getTempFile() {
        return temporaryFileResource.getTempFile();
    }
}
