package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.properties.NotificationProperties;
import lu.luxtrust.flowers.service.EmailSender;
import lu.luxtrust.flowers.service.EmailSenderFactory;
import lu.luxtrust.flowers.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.*;

import static lu.luxtrust.flowers.properties.NotificationProperties.NotificationConfig;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final TemplateEngine templateEngine;
    private final NotificationProperties notificationProperties;
    private final EmailSenderFactory emailSenderFactory;

    @Autowired
    public NotificationServiceImpl(EmailSenderFactory emailSenderFactory,
                                   NotificationProperties notificationProperties,
                                   TemplateEngine templateEngine) {
        this.notificationProperties = notificationProperties;
        this.templateEngine = templateEngine;
        this.emailSenderFactory = emailSenderFactory;
    }

    @Override
    public void notifyEndUserToValidateOrder(OrderUserValidatePage order) {
        notifyEndUserToValidateOrder(Collections.singletonList(order));
    }

    @Override
    public void notifyEndUserToValidateOrder(Collection<OrderUserValidatePage> orders) {
        EmailSender emailSender = emailSenderFactory.newMailSender();
        for (OrderUserValidatePage order: orders) {
            emailSender.newMessage()
                    .subject(notificationProperties.getEndUser().getDefaultSubject())
                    .to(order.getOrder().getHolder().getNotifyEmail())
                    .text(endUserValidateBody(order));
        }
        emailSender.send();
    }

    private String endUserValidateBody(OrderUserValidatePage order) {
        Context context = new Context();
        context.setVariable("baseLink", notificationProperties.getAppBaseLink());
        context.setVariable("pageHash", order.getPageHash());

        return templateEngine.process(notificationProperties.getEndUser().getBodyTemplateName(), context);
    }

    @Override
    public void notifyEndUserToActivateCertificate(CertificateOrder order) {
        emailSenderFactory.newMailSender()
                .newMessage()
                .subject(notificationProperties.getEndUserInviteToActivate().getDefaultSubject())
                .to(order.getHolder().getNotifyEmail())
                .text(templateEngine.process(notificationProperties.getEndUserInviteToActivate().getBodyTemplateName(), new Context()))
                .send();
    }

    @Override
    public void notify(List<String> emails, NotificationConfig config) {
        notify(Collections.emptyMap(), emails, config);
    }

    @Override
    public void notify(Map<String, Object> data, List<String> emails, NotificationConfig config) {
        if (emails == null || emails.isEmpty()) {
            throw new FlowersException("email list must be not empty");
        }
        emailSenderFactory.newMailSender()
                .newMessage()
                .subject(config.getDefaultSubject())
                .to(emails.toArray(new String[emails.size()]))
                .text(resolveVariables(data, config))
                .send();
    }

    private String resolveVariables(Map<String, Object> data, NotificationConfig nc) {
        Context context = new Context();
        context.setVariable("baseLink", notificationProperties.getAppBaseLink());
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        return templateEngine.process(nc.getBodyTemplateName(), context);
    }
}
