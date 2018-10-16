package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EsealOrderIsManagerOfEvaluator extends AbstractCertificateIsManagerOfEvaluator<ESealOrder> {

    @Autowired
    public EsealOrderIsManagerOfEvaluator(ESealOrderRepository eSealOrderRepository) {
        super(eSealOrderRepository);
    }

    @Override
    public Class<ESealOrder> supportedType() {
        return ESealOrder.class;
    }
}
