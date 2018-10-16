package lu.luxtrust.flowers.integration.notif;

import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.properties.NotifProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;

@Service
public class NotifMessageSenderImpl implements NotifMessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(NotifMessageSenderImpl.class);

    private final NotifProperties notifProperties;
    private final RestTemplate restTemplate;

    @Autowired
    public NotifMessageSenderImpl(NotifProperties notifProperties, RestTemplate restTemplate) {
        this.notifProperties = notifProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(NotifMessage message) {
        try {
            ResponseEntity<Object> result = restTemplate.postForEntity(notifProperties.getEndpoint().toURI(), message, Object.class);
            LOG.info("Notif service status for the message {} is {}, notif id={}, ", message, result.getStatusCode(), result.getBody());
            if (!result.getStatusCode().is2xxSuccessful()) {
                throw new FlowersException(String.format("Cant send the following message %s by notif service", message));
            }
        } catch (RestClientException | URISyntaxException e) {
            LOG.error("Cant send the following message {} by notif service", message, e);
            throw new FlowersException(e, String.format("Cant send the following message %s by notif service", message));
        }
    }
}
