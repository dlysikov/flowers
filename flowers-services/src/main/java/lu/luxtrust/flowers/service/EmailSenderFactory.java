package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.properties.NotifProperties;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderFactory {

    private final NotifProperties notifProperties;
    private final BeanFactory beanFactory;

    @Autowired
    public EmailSenderFactory(NotifProperties notifProperties, BeanFactory beanFactory) {
        this.notifProperties = notifProperties;
        this.beanFactory = beanFactory;
    }

    public EmailSender newMailSender() {
        return beanFactory.getBean((notifProperties.getEnabled() ? "notif" : "smtp") + "EmailSender", EmailSender.class);
    }
}
