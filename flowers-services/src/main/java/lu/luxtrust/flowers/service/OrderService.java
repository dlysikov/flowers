package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.Certificate;
import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.model.PageParams;

import java.util.List;

public interface OrderService<O extends Order> {

    void sendToLrs(O order) throws Exception;

    void updateStatus(String orderNumber, OrderStatus currentStatus, OrderStatus newStatus);

    O enrichOrder(String orderNumber, String ssn, List<Certificate> certificates);

}
