package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.integration.notif.NotifMessage;
import lu.luxtrust.flowers.integration.notif.NotifMessageSender;
import lu.luxtrust.flowers.service.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Scope("prototype")
@Component("notifEmailSender")
public class NotifEmailSender implements EmailSender<NotifEmailSender> {

    private NotifMessageSender notifMessageSender;

    private Map<NotifMessage, String[]> messages;
    private NotifMessage message;

    @Autowired
    public NotifEmailSender(NotifMessageSender notifMessageSender) {
        this.notifMessageSender = notifMessageSender;
        this.messages = new HashMap<>();
    }

    @Override
    public NotifEmailSender newMessage() {
        this.message = new NotifMessage();
        this.messages.put(message, null);
        return this;
    }

    @Override
    public NotifEmailSender subject(String subject) {
        this.message.setSubject(subject);
        return this;
    }

    @Override
    public NotifEmailSender to(String[] to) {
        this.messages.put(message, to);
        return this;
    }

    @Override
    public NotifEmailSender to(String to) {
        this.messages.put(message, new String[] {to});
        return this;
    }

    @Override
    public NotifEmailSender text(String text) {
        this.message.setContent(text);
        return this;
    }

    @Override
    public void send() {
        this.messages.forEach((m,d) -> {
            for (String destination: d) {
                notifMessageSender.send(new NotifMessage(m.getContent(), destination, m.getSubject(), NotifMessage.MessageType.EMAIL_HTML));
            }
        });
        this.messages.clear();
    }
}
