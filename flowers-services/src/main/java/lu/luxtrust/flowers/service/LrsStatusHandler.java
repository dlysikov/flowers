package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.integration.lrs.ws.GetStatusResponse;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSOrderStatusEnum;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSStatusResult;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LrsStatusHandler implements AsyncHandler<GetStatusResponse> {

    private final Logger LOGGER = getLogger();

    private final OrderService service;
    private final String orderNumber;
    private final OrderStatus currentStatus;

    LrsStatusHandler(OrderService service,
                     String orderNumber,
                     OrderStatus currentStatus) {
        this.service = service;
        this.orderNumber = orderNumber;
        this.currentStatus = currentStatus;
    }

    @Override
    public void handleResponse(Response<GetStatusResponse> res) {
        OrderStatus orderStatus;
        try {
            LOGGER.debug("retrieving status for lrs_order_number {}", orderNumber);
            LrsWSStatusResult result = res.get().getReturn();
            if (result.getStatus() == null) {
                LOGGER.error("failed to retrieve status for lrs_order_number {}, reason: {}", orderNumber, result.getErrorMessage());
                return;
            }
            orderStatus = resolveStatus(result.getStatus());
            if (orderStatus == null) {
                LOGGER.error("unsupported lrs status {} for lrs_order_number {}", result.getStatus(), orderNumber);
                return;
            }
            if (currentStatus == orderStatus) {
                LOGGER.debug("no status changes for lrs_order_number {}", orderNumber);
                return;
            }
            service.updateStatus(orderNumber, currentStatus, orderStatus);
        } catch (Exception e) {
            LOGGER.error("failed to update status for lrs_order_number {}", orderNumber, e);
        }
    }

    public String getLrsOrderNumber() {
        return orderNumber;
    }

    abstract OrderStatus resolveStatus(LrsWSOrderStatusEnum lrsStatus);

    abstract Logger getLogger();

    public static class ESeal extends LrsStatusHandler {

        public ESeal(OrderService service, String orderNumber, OrderStatus currentStatus) {
            super(service, orderNumber, currentStatus);
        }

        @Override
        OrderStatus resolveStatus(LrsWSOrderStatusEnum lrsStatus) {
            switch(lrsStatus) {
                case ONGOING: return OrderStatus.LRS_ONGOING;
                case WAITING: return OrderStatus.LRS_ONGOING;
                case PRODUCED:
                case ACTIVATED: return OrderStatus.LRS_PRODUCED;
                case CANCELED: return OrderStatus.LRS_CANCELED;
                default: return null;
            }
        }

        @Override
        Logger getLogger() {
            return LoggerFactory.getLogger(LrsStatusHandler.ESeal.class);
        }
    }

    public static class Certificate extends LrsStatusHandler {

        public Certificate(OrderService service, String orderNumber, OrderStatus currentStatus) {
            super(service, orderNumber, currentStatus);
        }

        @Override
        OrderStatus resolveStatus(LrsWSOrderStatusEnum lrsStatus) {
            switch(lrsStatus) {
                case ONGOING: return OrderStatus.LRS_ONGOING;
                case WAITING: return OrderStatus.LRS_ONGOING;
                case PRODUCED: return OrderStatus.LRS_PRODUCED;
                case ACTIVATED: return OrderStatus.LRS_ACTIVATED;
                case CANCELED: return OrderStatus.LRS_CANCELED;
                default: return null;
            }
        }

        @Override
        Logger getLogger() {
            return LoggerFactory.getLogger(LrsStatusHandler.Certificate.class);
        }
    }
}
