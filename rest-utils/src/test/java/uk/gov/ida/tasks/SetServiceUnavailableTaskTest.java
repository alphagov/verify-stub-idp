package uk.gov.ida.tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ida.configuration.ServiceStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class SetServiceUnavailableTaskTest {

    ServiceStatus instance;
    SetServiceUnavailableTask setServiceUnavailableTask;

    @Before
    public void setUp() {
        instance = ServiceStatus.getInstance();
        setServiceUnavailableTask = new SetServiceUnavailableTask(instance);
    }

    @Test
    public void shouldCreateSetServiceTaskAndReturnFalseWhenExecuted() throws Exception {
        assertThat(setServiceUnavailableTask.getName()).isEqualTo("set-service-unavailable");
        assertThat(instance.isServerStatusOK()).isEqualTo(true);
        setServiceUnavailableTask.execute(null, null);
        assertThat(instance.isServerStatusOK()).isEqualTo(false);
    }

    @After
    public void tearDown(){
        instance.setServiceStatus(true);
    }
}