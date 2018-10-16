package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CertificateOrderIsManagerOfEvaluator extends AbstractCertificateIsManagerOfEvaluator<CertificateOrder> {

    @Autowired
    public CertificateOrderIsManagerOfEvaluator(CertificateOrderRepository certificateOrderRepository) {
        super(certificateOrderRepository);
    }

    @Override
    public Class<CertificateOrder> supportedType() {
        return CertificateOrder.class;
    }
}
