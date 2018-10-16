package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.model.SMS;

import java.util.Collection;
import java.util.List;

public interface OrderValidationPageService {
    OrderUserValidatePage generateValidationPage(CertificateOrder order);
    List<OrderUserValidatePage> generateValidationPages(Collection<CertificateOrder> order);
    SMS generateMobileValidationSMS(OrderUserValidatePage page);
}
