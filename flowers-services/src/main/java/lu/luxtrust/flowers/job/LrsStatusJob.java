package lu.luxtrust.flowers.job;

import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.repository.OrderRepository;
import lu.luxtrust.flowers.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public abstract class LrsStatusJob<O extends Order> {

    private static final Logger LOG = LoggerFactory.getLogger(LrsStatusJob.class);

    private final OrderRepository<O> orderRepository;
    private final OrderService<O> orderService;
    private final LrsService lrsService;

    public LrsStatusJob(OrderRepository<O> orderRepository,
                        LrsService lrsService,
                        OrderService<O> orderService) {
        this.orderRepository = orderRepository;
        this.lrsService = lrsService;
        this.orderService = orderService;
    }

    @Scheduled(cron = "${job.lrs.status-poll.period:0 0/10 * ? * *}")
    public void pollLrsStatuses() {
        LOG.info("LRS status polling job started...");
        pollLrsStatus(OrderStatus.LRS_PRODUCED);
        pollLrsStatus(OrderStatus.LRS_ONGOING);
        pollLrsStatus(OrderStatus.SENT_TO_LRS);
        LOG.info("LRS status polling job finished");
    }

    private void pollLrsStatus(OrderStatus status) {
        LOG.info("Polling orders in {} status", status);
        List<String> orderNumbers = orderRepository.findLrsOrderNumbersForStatus(status);
        LOG.info("Found {} orders in status {} to poll LRS status", orderNumbers.size(), status);
        for (String orderNumber : orderNumbers) {
            lrsService.pollStatus(getLrsStatusHandler(orderService, orderNumber, status));
        }
    }

    protected abstract LrsStatusHandler getLrsStatusHandler(OrderService<O> orderService, String orderNumber, OrderStatus status);
}
