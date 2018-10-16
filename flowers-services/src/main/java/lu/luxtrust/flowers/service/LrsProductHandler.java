package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.Certificate;
import lu.luxtrust.flowers.integration.lrs.ws.GetProductResponse;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSCertificate;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSProduct;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSProductResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.List;

public class LrsProductHandler implements AsyncHandler<GetProductResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LrsProductHandler.class);

    private final OrderService service;
    private final String orderNumber;

    public LrsProductHandler(OrderService service, String orderNumber) {
        this.service = service;
        this.orderNumber = orderNumber;
    }

    @Override
    public void handleResponse(Response<GetProductResponse> res) {
        try {
            LOGGER.debug("retrieving product details for lrs_order_number {}", orderNumber);
            LrsWSProductResult result = res.get().getReturn();
            if (result.getProduct() == null) {
                LOGGER.error("failed to retrieve product data for lrs_order_number {}, reason: {}", orderNumber, result.getErrorMessage());
                return;
            }
            LrsWSProduct product = result.getProduct();
            List<Certificate> certificates = new ArrayList<>();
            for (LrsWSCertificate lrsWSCertificate : product.getCertificate()) {
                Certificate certificate = new Certificate();
                certificate.setCsn(lrsWSCertificate.getSerial());
                certificates.add(certificate);
            }
            service.enrichOrder(orderNumber, product.getSubjectSerialNumber(), certificates);
        } catch (Exception e) {
            LOGGER.error("failed to get product details for lrs_order_number {}", orderNumber, e);
        }
    }

    public String getLrsOrderNumber() {
        return orderNumber;
    }
}