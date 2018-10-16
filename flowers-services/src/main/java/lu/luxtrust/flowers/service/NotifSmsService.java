package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.integration.notif.NotifMessage;
import lu.luxtrust.flowers.integration.notif.NotifMessageSender;
import lu.luxtrust.flowers.model.SMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class NotifSmsService implements SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(NotifSmsService.class);

    private final NotifMessageSender notifMessageSender;

    @Autowired
    public NotifSmsService(NotifMessageSender notifMessageSender) {
        this.notifMessageSender = notifMessageSender;
    }

    @Override
    public SMS send(SMS sms) {
        return send(Collections.singletonList(sms)).get(0);
    }

    @Override
    public List<SMS> send(List<SMS> sms) {
        sms.forEach(s -> {
            try {
                LOG.info("Sending sms {}", s);
                this.notifMessageSender.send(new NotifMessage(s.getContent(), s.getMobileNumber(), s.getSubject(), NotifMessage.MessageType.SMS));
                s.setStatus(SMS.Status.SENT);
                LOG.info("SMS was successfully sent. {}", s);
            } catch (Exception e) {
                LOG.error("Failed to sent sms {}, due to ", s, e);
                s.setStatus(SMS.Status.FAILED);
            }
        });
        return sms;
    }
}
