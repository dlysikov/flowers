package lu.luxtrust.flowers.security.method.impl;

import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.repository.OrderRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.method.IsManagerOfEvaluator;

public abstract class AbstractCertificateIsManagerOfEvaluator<T extends Order> implements IsManagerOfEvaluator<T, RestAuthenticationToken> {

    private final OrderRepository<T> orderRepository;

    public AbstractCertificateIsManagerOfEvaluator(OrderRepository<T> orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public boolean isManagerOf(RestAuthenticationToken authentication, Long id) {
        return isManagerOf(orderRepository.findUnitByOrderId(id), authentication);
    }

    @Override
    public boolean isManagerOf(RestAuthenticationToken authentication, T targetObject) {
        return isManagerOf(targetObject.getUnit(), authentication);
    }


    private boolean isManagerOf(Unit unit, RestAuthenticationToken authentication) {
        if (unit == null) {
            return Boolean.TRUE;
        }
        if (authentication.getUnit() != null) {
            return authentication.getUnit().getId().equals(unit.getId());
        }
        return Boolean.TRUE;
    }
}
