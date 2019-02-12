package uk.gov.ida.saml.core.transformers;

import org.junit.Test;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.extensions.EidasAuthnContext;
import uk.gov.ida.saml.core.extensions.IdaAuthnContext;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

public class AuthnContextFactoryTest {

    private final AuthnContextFactory factory = new AuthnContextFactory();

    @Test
    public void shouldBeAbleToMapFromEidasToLoA() {
        assertThat(factory.mapFromEidasToLoA(EidasAuthnContext.EIDAS_LOA_LOW)).isEqualTo(AuthnContext.LEVEL_1);
        assertThat(factory.mapFromEidasToLoA(EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL)).isEqualTo(AuthnContext.LEVEL_2);
        assertThat(factory.mapFromEidasToLoA(EidasAuthnContext.EIDAS_LOA_HIGH)).isEqualTo(AuthnContext.LEVEL_2);
    }

    @Test
    public void shouldBeAbleToMapFromLoAtoEidas() {
        assertThat(factory.mapFromLoAToEidas(AuthnContext.LEVEL_1)).isEqualTo(EidasAuthnContext.EIDAS_LOA_LOW);
        assertThat(factory.mapFromLoAToEidas(AuthnContext.LEVEL_2)).isEqualTo(EidasAuthnContext.EIDAS_LOA_SUBSTANTIAL);
        assertThatThrownBy(() -> factory.mapFromLoAToEidas(AuthnContext.LEVEL_3)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> factory.mapFromLoAToEidas(AuthnContext.LEVEL_4)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> factory.mapFromLoAToEidas(AuthnContext.LEVEL_X)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldThrowExceptionWhenMappingInvalidEidasToLoA() {
        final String levelOfAssurance = "glarg";
        try {
            factory.mapFromEidasToLoA(levelOfAssurance);
            fail("Expected an exception but none was thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo(format(AuthnContextFactory.LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE, levelOfAssurance));
        }
    }

    @Test
    public void shouldCorrectlyTransformValidValues() {
        assertThat(factory.authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_1_AUTHN_CTX)).isEqualTo(AuthnContext.LEVEL_1);
        assertThat(factory.authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_2_AUTHN_CTX)).isEqualTo(AuthnContext.LEVEL_2);
        assertThat(factory.authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_3_AUTHN_CTX)).isEqualTo(AuthnContext.LEVEL_3);
        assertThat(factory.authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_4_AUTHN_CTX)).isEqualTo(AuthnContext.LEVEL_4);
        assertThat(factory.authnContextForLevelOfAssurance(IdaAuthnContext.LEVEL_X_AUTHN_CTX)).isEqualTo(AuthnContext.LEVEL_X);
    }

    @Test
    public void shouldThrowExceptionIfInvalidValue() {
        final String levelOfAssurance = "glarg";
        try {
            factory.authnContextForLevelOfAssurance(levelOfAssurance);
            fail("Expected an exception but none was thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo(format(AuthnContextFactory.LEVEL_OF_ASSURANCE_IS_NOT_A_RECOGNISED_VALUE, levelOfAssurance));
        }
    }

}
