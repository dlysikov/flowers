package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.integration.notif.NotifMessage;
import lu.luxtrust.flowers.integration.notif.NotifMessageSender;
import lu.luxtrust.flowers.model.SMS;
import org.aspectj.weaver.ast.Not;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotifSmsServiceTest {

    @Mock
    private NotifMessageSender notifMessageSender;

    private NotifSmsService target;

    @Before
    public void init() {
        this.target = new NotifSmsService(notifMessageSender);
    }

    @Test
    public void sendSuccess() {
        SMS sms = new SMS("test", "test", "test");
        SMS send = target.send(sms);
        assertThat(send.getStatus()).isEqualTo(SMS.Status.SENT);
        ArgumentCaptor<NotifMessage> nf = ArgumentCaptor.forClass(NotifMessage.class);
        verify(notifMessageSender).send(nf.capture());

        NotifMessage nfM = nf.getValue();
        assertThat(nfM.getContent()).isEqualTo(sms.getContent());
        assertThat(nfM.getDestination()).isEqualTo(sms.getMobileNumber());
        assertThat(nfM.getSubject()).isEqualTo(sms.getSubject());
    }

    @Test
    public void sendFailed() {
        SMS sms = new SMS("test", "test", "test");
        doThrow(new RuntimeException()).when(notifMessageSender).send(any(NotifMessage.class));
        SMS send = target.send(sms);

        assertThat(send.getStatus()).isEqualTo(SMS.Status.FAILED);
    }
}