package uk.gov.ida.saml.idp.stub.tranformers.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.jodatime.api.Assertions.assertThat;
import static uk.gov.ida.saml.idp.builders.IdentityProviderAuthnStatementBuilder.anIdentityProviderAuthnStatement;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.AuthnStatement;
import uk.gov.ida.saml.core.OpenSamlXmlObjectFactory;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.core.test.OpenSAMLRunner;
import uk.gov.ida.saml.idp.stub.transformers.outbound.IdentityProviderAuthnStatementToAuthnStatementTransformer;
import uk.gov.ida.shared.utils.datetime.DateTimeFreezer;

@RunWith(OpenSAMLRunner.class)
public class IdentityProviderAuthnStatementToAuthnStatementTransformerTest {

    private IdentityProviderAuthnStatementToAuthnStatementTransformer transformer;

    @Before
    public void setup() {
        DateTimeFreezer.freezeTime();
        transformer = new IdentityProviderAuthnStatementToAuthnStatementTransformer(new OpenSamlXmlObjectFactory());
    }

    @Test
    public void shouldTransformAuthnStatementWithLevel1() {
        verifyLevel(AuthnContext.LEVEL_1, IdaAuthnContext.LEVEL_1_AUTHN_CTX);
    }

    @Test
    public void shouldTransformAuthnStatementWithLevel2() {
        verifyLevel(AuthnContext.LEVEL_2, IdaAuthnContext.LEVEL_2_AUTHN_CTX);
    }

    @Test
    public void shouldTransformAuthnStatementWithLevel3() {
        verifyLevel(AuthnContext.LEVEL_3, IdaAuthnContext.LEVEL_3_AUTHN_CTX);
    }

    @Test
    public void shouldTransformAuthnStatementWithLevel4() {
        verifyLevel(AuthnContext.LEVEL_4, IdaAuthnContext.LEVEL_4_AUTHN_CTX);
    }

    @Test
    public void shouldTransformAuthnStatementWithLevelX() {
        verifyLevel(AuthnContext.LEVEL_X, IdaAuthnContext.LEVEL_X_AUTHN_CTX);
    }

    private void verifyLevel(AuthnContext authnContext, String expectedLevel) {
        IdentityProviderAuthnStatement originalAuthnStatement = anIdentityProviderAuthnStatement()
                .withAuthnContext(authnContext)
                .build();

        AuthnStatement transformedAuthnStatement =
                transformer.transform(originalAuthnStatement);

        assertThat(transformedAuthnStatement.getAuthnInstant()).isEqualTo(DateTime.now());
        assertThat(transformedAuthnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef()).isEqualTo(expectedLevel);
    }

    @After
    public void after(){
        DateTimeFreezer.unfreezeTime();
    }
}
