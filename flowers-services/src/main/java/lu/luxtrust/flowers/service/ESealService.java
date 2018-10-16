package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;

import javax.mail.MessagingException;

public interface ESealService extends OrderService<ESealOrder> {

    PageResponse<ESealOrder> findAll(Long userId, PageParams params);

    boolean isValuableDataModifiable(Long id);

    ESealOrder save(ESealOrder order);

}
