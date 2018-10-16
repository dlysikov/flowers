package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.entity.enrollment.Certificate;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.OrderRepository;
import lu.luxtrust.flowers.service.LrsProductHandler;
import lu.luxtrust.flowers.service.LrsService;
import lu.luxtrust.flowers.service.OrderService;
import lu.luxtrust.flowers.service.PredicateProducer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public abstract class OrderServiceImpl<O extends Order> implements OrderService<O> {

    private final OrderRepository<O> orderRepository;
    private final LrsService lrsService;

    protected OrderServiceImpl(OrderRepository<O> orderRepository, LrsService lrsService) {
        this.orderRepository = orderRepository;
        this.lrsService = lrsService;
    }

    public PageResponse<O> findAll(PageParams params, boolean distinct, PredicateProducer...pp) {
        Sort sortOrder = new Sort(Sort.Direction.DESC, "id");
        List<O> data;
        Long count = orderRepository.count(SearchSpecifications.filterQuery(params.getFilter(), distinct, pp));
        if (params.getPageSize() != null && params.getPageNumber() != null) {
            PageRequest pageRequest = new PageRequest(params.getPageNumber() - 1, params.getPageSize(), sortOrder);
            data = Lists.newArrayList(orderRepository.findAll(SearchSpecifications.filterQuery(params.getFilter(), distinct, pp), pageRequest));
        } else {
            data = orderRepository.findAll(SearchSpecifications.filterQuery(params.getFilter(), distinct, pp), sortOrder);
        }
        return new PageResponse<>(data, count);
    }

    @Override
    public void updateStatus(String orderNumber, OrderStatus currentStatus, OrderStatus newStatus) {
        orderRepository.updateStatus(newStatus, orderNumber);
        if (newStatus == OrderStatus.LRS_PRODUCED) {
            lrsService.enrichProductInfo(new LrsProductHandler(this, orderNumber));
        }
        if (newStatus == OrderStatus.LRS_ACTIVATED) {
            String ssn = orderRepository.getSsnByLrsOrderNumber(orderNumber);
            if (StringUtils.isEmpty(ssn)) {
                lrsService.enrichProductInfo(new LrsProductHandler(this, orderNumber));
            }
        }
    }

    @Override
    public O enrichOrder(String orderNumber, String ssn, List<Certificate> certificates) {
        O order = orderRepository.findOneByLrsOrderNumber(orderNumber);
        order.setSsn(ssn);
        order.setCertificates(certificates);
        for (Certificate certificate : certificates) {
            certificate.setOrder(order);
        }
        return orderRepository.save(order);
    }

}
