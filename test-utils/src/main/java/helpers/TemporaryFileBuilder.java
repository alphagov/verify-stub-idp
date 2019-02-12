package helpers;

import java.io.File;

public class TemporaryFileBuilder {
    private TemporaryFileResourceBuilder temporaryFileResourceBuilder = TemporaryFileResourceBuilder.aTemporaryFileResource();

    public static TemporaryFileBuilder aTemporaryFile() {
        return new TemporaryFileBuilder();
    }

    public TemporaryFileBuilder content(String content) {
        temporaryFileResourceBuilder.content(content);
        return this;
    }

    public TemporaryFileBuilder content(byte[] content) {
        temporaryFileResourceBuilder.content(content);
        return this;
    }

    public TemporaryFileBuilder file(File file) {
        temporaryFileResourceBuilder.file(file);
        return this;
    }

    public TemporaryFile build() {
        return new TemporaryFile(temporaryFileResourceBuilder.build());
    }
}