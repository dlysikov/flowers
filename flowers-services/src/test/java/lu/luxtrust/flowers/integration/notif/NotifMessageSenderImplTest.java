package lu.luxtrust.flowers.integration.notif;

import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.properties.NotifProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotifMessageSenderImplTest {

    @Mock
    private NotifProperties notifProperties;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity<Object> result;

    private NotifMessageSenderImpl target;
    private URL url;

    @Before
    public void init() throws Exception {
        url = new URL("http://test.com");
        when(notifProperties.getEndpoint()).thenReturn(url);
        this.target = new NotifMessageSenderImpl(notifProperties, restTemplate);
    }

    @Test
    public void sendSuccess() {
        when(restTemplate.postForEntity(any(URI.class), any(NotifMessage.class), any(Class.class))).thenReturn(result);
        when(result.getStatusCode()).thenReturn(HttpStatus.OK);

        target.send(new NotifMessage("test", "test", "test", NotifMessage.MessageType.SMS));
        verify(restTemplate).postForEntity(any(URI.class), any(NotifMessage.class), any(Class.class));
    }

    @Test(expected = FlowersException.class)
    public void sendFailedHttpStatus() {
        when(restTemplate.postForEntity(any(URI.class), any(NotifMessage.class), any(Class.class))).thenReturn(result);
        when(result.getStatusCode()).thenReturn(HttpStatus.UNPROCESSABLE_ENTITY);

        target.send(new NotifMessage("test", "test", "test", NotifMessage.MessageType.SMS));
    }

    @Test(expected = FlowersException.class)
    public void sendFailedException() {
        when(restTemplate.postForEntity(any(URI.class), any(NotifMessage.class), any(Class.class))).thenThrow(new RestClientException("TEST"));

        target.send(new NotifMessage("test", "test", "test", NotifMessage.MessageType.SMS));
    }
}