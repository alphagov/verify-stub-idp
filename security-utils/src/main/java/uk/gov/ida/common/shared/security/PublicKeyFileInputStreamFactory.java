package uk.gov.ida.common.shared.security;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.google.common.base.Throwables.propagate;

public class PublicKeyFileInputStreamFactory implements PublicKeyInputStreamFactory {

    @Inject
    public PublicKeyFileInputStreamFactory() { }

    public InputStream createInputStream(String publicKeyUri) {
        try {
            return new FileInputStream(new File(publicKeyUri));
        } catch (FileNotFoundException e) {
            throw propagate(e);
        }
    }
}
