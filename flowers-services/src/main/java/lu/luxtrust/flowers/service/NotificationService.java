package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static lu.luxtrust.flowers.properties.NotificationProperties.NotificationConfig;

public interface NotificationService {

    void notifyEndUserToValidateOrder(OrderUserValidatePage order);

    void notifyEndUserToValidateOrder(Collection<OrderUserValidatePage> orders);

    void notifyEndUserToActivateCertificate(CertificateOrder order);

    void notify(List<String> emails, NotificationConfig config);

    void notify(Map<String, Object> data, List<String> emails, NotificationConfig config);
}
