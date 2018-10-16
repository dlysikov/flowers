package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.integration.notif.NotifMessage;
import lu.luxtrust.flowers.integration.notif.NotifMessageSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotifEmailSenderTest {

    @Mock
    private NotifMessageSender notifMessageSender;

    @Test
    public void send() {
        NotifEmailSender target = new NotifEmailSender(this.notifMessageSender);

        target.newMessage()
                .to(new String[] {"to1", "to2"})
                .subject("test")
                .text("test")
                .send();

        ArgumentCaptor<NotifMessage> captor = ArgumentCaptor.forClass(NotifMessage.class);

        verify(notifMessageSender, times(2)).send(captor.capture());

        List<NotifMessage> messages = captor.getAllValues();
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getDestination()).isEqualTo("to1");
        assertThat(messages.get(1).getDestination()).isEqualTo("to2");

        for (NotifMessage nm: messages) {
            assertThat(nm.getSubject()).isEqualTo("test");
            assertThat(nm.getContent()).isEqualTo("test");
            assertThat(nm.getType()).isEqualTo(NotifMessage.MessageType.EMAIL_HTML);
        }
    }
}