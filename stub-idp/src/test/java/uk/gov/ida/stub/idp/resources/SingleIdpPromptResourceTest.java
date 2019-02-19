package uk.gov.ida.stub.idp.resources;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ida.saml.core.test.TestEntityIds;
import uk.gov.ida.stub.idp.configuration.SingleIdpConfiguration;
import uk.gov.ida.stub.idp.domain.Service;
import uk.gov.ida.stub.idp.exceptions.FeatureNotEnabledException;
import uk.gov.ida.stub.idp.repositories.AllIdpsUserRepository;
import uk.gov.ida.stub.idp.repositories.Idp;
import uk.gov.ida.stub.idp.repositories.IdpSessionRepository;
import uk.gov.ida.stub.idp.repositories.IdpStubsRepository;
import uk.gov.ida.stub.idp.resources.singleidp.SingleIdpPromptPageResource;
import uk.gov.ida.stub.idp.services.ServiceListService;
import uk.gov.ida.stub.idp.views.SingleIdpPromptPageView;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleIdpPromptResourceTest {

    private final URI verifySubmissionUri = URI.create("http://localhost/initiate-single-idp-journey");
    private final Service service1 = new Service("Service 1", "LEVEL_2", "service-id-1", "Category A");
    private final Service service2 = new Service("Service 2", "LEVEL_1", "service-id-2", "Category B");
    private final Service service3 = new Service("Service 3", "LEVEL_2", "service-id-3", "Category A");

    @BeforeClass
    public static void doALittleHackToMakeGuicierHappyForSomeReason() {
        JerseyGuiceUtils.reset();
    }

    @Mock
    private IdpStubsRepository idpStubsRepository;

    @Mock
    private SingleIdpConfiguration singleIdpConfiguration;

    @Mock
    private ServiceListService serviceListService;

    @Mock
    private AllIdpsUserRepository allIdpsUserRepository;

    @Mock
    private IdpSessionRepository idpSessionRepository;

    private SingleIdpPromptPageResource singleIdpPromptPageResource;

    private final String idpName = "idpName";
    private final Idp idp = new Idp(idpName, "Test Idp", "test-idp-asset-id", true, TestEntityIds.STUB_IDP_ONE, null);

    @Before
    public void setUp() {
        singleIdpPromptPageResource = new SingleIdpPromptPageResource(idpStubsRepository, serviceListService, singleIdpConfiguration, idpSessionRepository);
        when(singleIdpConfiguration.isEnabled()).thenReturn(true);
        when(singleIdpConfiguration.getVerifySubmissionUri()).thenReturn(verifySubmissionUri);
        when(idpStubsRepository.getIdpWithFriendlyId(idpName)).thenReturn(idp);
    }

    @Test
    public void shouldThrowIfFeatureDisabled() {
        when(singleIdpConfiguration.isEnabled()).thenReturn(false);

        assertThatExceptionOfType(FeatureNotEnabledException.class)
            .isThrownBy(()->singleIdpPromptPageResource.get(idpName, Optional.empty(),Optional.empty(), null));
    }


    @Test
    public void shouldReturnPageViewWithEmptyListIfHubReturnsNoServices() throws FeatureNotEnabledException {

        when(serviceListService.getServices()).thenReturn(new ArrayList<>());

        Response response = singleIdpPromptPageResource.get(idpName, Optional.empty(), Optional.empty(), null);

        assertThat(response.getEntity()).isInstanceOf(SingleIdpPromptPageView.class);

        SingleIdpPromptPageView promptView = (SingleIdpPromptPageView) response.getEntity();
        assertThat(promptView.getServices().size()).isEqualTo(0);
        assertThat(promptView.getVerifySubmissionUrl()).isEqualTo(verifySubmissionUri);
    }

    @Test
    public void shouldReturnPageViewWithSortedListIfHubReturnsPopulatedListOfServices() throws FeatureNotEnabledException {

        when(serviceListService.getServices()).thenReturn(Arrays.asList(service1, service2, service3));

        Response response = singleIdpPromptPageResource.get(idpName, Optional.empty(), Optional.empty(),null);

        assertThat(response.getEntity()).isInstanceOf(SingleIdpPromptPageView.class);

        SingleIdpPromptPageView promptView = (SingleIdpPromptPageView) response.getEntity();
        assertThat(promptView.getServices().size()).isEqualTo(3);
        assertThat(promptView.getVerifySubmissionUrl()).isEqualTo(verifySubmissionUri);
        assertThat(promptView.getIdpId()).isEqualTo(idp.getIssuerId());
        assertThat(promptView.getUniqueId()).isNotNull();

        // List should be sorted by category
        assertThat(promptView.getServices().get(0).getServiceCategory()).isEqualTo("Category A");
        assertThat(promptView.getServices().get(1).getServiceCategory()).isEqualTo("Category A");
        assertThat(promptView.getServices().get(2).getServiceCategory()).isEqualTo("Category B");
    }

}
