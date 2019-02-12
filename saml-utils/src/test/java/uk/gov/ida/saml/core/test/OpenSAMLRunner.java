package uk.gov.ida.saml.core.test;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import uk.gov.ida.saml.core.IdaSamlBootstrap;

public class OpenSAMLRunner extends BlockJUnit4ClassRunner {

    public OpenSAMLRunner(Class<?> klass) throws InitializationError {
        super(klass);
        try {
            IdaSamlBootstrap.bootstrap();
         } catch (IdaSamlBootstrap.BootstrapException e) {
            throw new InitializationError(e);
        }
    }
}
