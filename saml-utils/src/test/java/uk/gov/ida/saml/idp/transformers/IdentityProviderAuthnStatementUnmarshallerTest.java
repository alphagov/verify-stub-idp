package uk.gov.ida.saml.idp.transformers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import uk.gov.ida.saml.core.domain.IdentityProviderAuthnStatement;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;
import uk.gov.ida.saml.core.test.OpenSAMLMockitoRunner;
import uk.gov.ida.saml.core.transformers.AuthnContextFactory;
import uk.gov.ida.saml.core.transformers.IdentityProviderAuthnStatementUnmarshaller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static uk.gov.ida.saml.core.test.builders.AssertionBuilder.anAssertion;
import static uk.gov.ida.saml.core.test.builders.AttributeStatementBuilder.anAttributeStatement;
import static uk.gov.ida.saml.core.test.builders.AuthnContextBuilder.anAuthnContext;
import static uk.gov.ida.saml.core.test.builders.AuthnContextClassRefBuilder.anAuthnContextClassRef;
import static uk.gov.ida.saml.core.test.builders.AuthnStatementBuilder.anAuthnStatement;
import static uk.gov.ida.saml.core.test.builders.IPAddressAttributeBuilder.anIPAddress;


@RunWith(OpenSAMLMockitoRunner.class)
public class IdentityProviderAuthnStatementUnmarshallerTest {

    @Mock
    private AuthnContextFactory authnContextFactory;
    public IdentityProviderAuthnStatementUnmarshaller unmarshaller;

    @Before
    public void setUp() throws Exception {
        unmarshaller = new IdentityProviderAuthnStatementUnmarshaller(
                authnContextFactory);
    }

    @Test
    public void transform_shouldTransformAuthnStatement() throws Exception {
        AuthnContextClassRef authnContextClassRef = anAuthnContextClassRef().withAuthnContextClasRefValue(IdaAuthnContext.LEVEL_3_AUTHN_CTX).build();
        AuthnContext authnContext = anAuthnContext()
                .withAuthnContextClassRef(authnContextClassRef)
                .build();

        AuthnStatement authnStatement = anAuthnStatement()
                .withAuthnContext(authnContext)
                .build();

        Assertion assertion = anAssertion().addAuthnStatement(authnStatement).buildUnencrypted();

        unmarshaller.fromAssertion(assertion);

        verify(authnContextFactory).authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_3_AUTHN_CTX);
    }

    @Test
    public void transform_shouldTransformClientIpAddressWhenAssertionContainsAuthnStatement() throws Exception {
        String ipAddress = "1.2.3.4";
        Assertion assertion = anAssertion()
                .addAuthnStatement(anAuthnStatement().build())
                .addAttributeStatement(anAttributeStatement()
                        .addAttribute(anIPAddress().withValue(ipAddress).build())
                        .build())
                .buildUnencrypted();

        IdentityProviderAuthnStatement authnStatement = unmarshaller.fromAssertion(assertion);

        assertThat(authnStatement.getUserIpAddress().getStringValue()).isEqualTo(ipAddress);
    }
}
