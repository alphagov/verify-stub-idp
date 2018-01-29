package uk.gov.ida.stub.idp.views;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import uk.gov.ida.saml.core.domain.Address;
import uk.gov.ida.saml.core.domain.AuthnContext;
import uk.gov.ida.saml.core.domain.SimpleMdsValue;
import uk.gov.ida.stub.idp.domain.IdpUser;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EidasConsentView extends IdpPageView {
    public EidasConsentView(String name, String idpId, String assetId) {
        super("eidasConsent.ftl", name, idpId, null, assetId);
    }

    public String getPageTitle() {
        return String.format("Consent page for %s", getName());
    }
}
