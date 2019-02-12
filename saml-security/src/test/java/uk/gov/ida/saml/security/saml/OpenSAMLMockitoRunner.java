package uk.gov.ida.saml.security.saml;

import org.junit.runners.model.InitializationError;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;

public class OpenSAMLMockitoRunner extends MockitoJUnitRunner {

    public OpenSAMLMockitoRunner(Class<?> klass) throws InitializationError, InvocationTargetException {
        super(klass);
        try {
            IdaSamlBootstrap.bootstrap();
         } catch (IdaSamlBootstrap.BootstrapException e) {
            throw new InitializationError(e);
        }
    }
}
