package lu.luxtrust.flowers.integration.lrs.mock;

import lu.luxtrust.flowers.integration.lrs.ws.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.*;
import java.util.concurrent.*;

public class LrsWsMock implements LrsWS {

    private static final Logger LOG = LoggerFactory.getLogger(LrsWsMock.class);

    private Map<String, LrsWSOrderStatusEnum> status = new HashMap<>();
    private Map<String, String> orderId2LrsNumber = new HashMap<>();

    @Override
    public Response<SendCodesResponse> sendCodesAsync(LrsWSRegistrationAuthority ra, String order) {
        return null;
    }

    @Override
    public Future<?> sendCodesAsync(LrsWSRegistrationAuthority ra, String order, AsyncHandler<SendCodesResponse> asyncHandler) {
        return null;
    }

    @Override
    public LrsWSCodesResult sendCodes(LrsWSRegistrationAuthority ra, String order) {
        return null;
    }

    @Override
    public Response<GetOrderResponse> getOrderAsync(LrsWSRegistrationAuthority ra, String ssn) {
        return null;
    }

    @Override
    public Future<?> getOrderAsync(LrsWSRegistrationAuthority ra, String ssn, AsyncHandler<GetOrderResponse> asyncHandler) {
        return null;
    }

    @Override
    public LrsWSOrderResult getOrder(LrsWSRegistrationAuthority ra, String ssn) {
        return null;
    }

    @Override
    public Response<CancelResponse> cancelAsync(LrsWSRegistrationAuthority ra, String order, LrsWSSignature rao, String version) {
        return null;
    }

    @Override
    public Future<?> cancelAsync(LrsWSRegistrationAuthority ra, String order, LrsWSSignature rao, String version, AsyncHandler<CancelResponse> asyncHandler) {
        return null;
    }

    @Override
    public LrsWSCancelResult cancel(LrsWSRegistrationAuthority ra, String order, LrsWSSignature rao, String version) {
        return null;
    }

    @Override
    public Response<GetVersionResponse> getVersionAsync() {
        return null;
    }

    @Override
    public Future<?> getVersionAsync(AsyncHandler<GetVersionResponse> asyncHandler) {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Response<GetProductResponse> getProductAsync(LrsWSRegistrationAuthority ra, String order) {
        LrsWSProductResult result = new LrsWSProductResult();
        MockProduct product = new MockProduct();
        product.setSubjectSerialNumber(UUID.randomUUID().toString());
        LrsWSCertificate certificate = new LrsWSCertificate();
        certificate.setSerial(UUID.randomUUID().toString());
        product.setCertificates(Collections.singletonList(certificate));
        result.setProduct(product);
        GetProductResponse response = new GetProductResponse();
        response.setReturn(result);
        return new MockResponse<>(response);
    }

    @Override
    public Future<?> getProductAsync(LrsWSRegistrationAuthority ra, String order, AsyncHandler<GetProductResponse> asyncHandler) {
        asyncHandler.handleResponse(getProductAsync(ra, order));
        return null;
    }

    @Override
    public LrsWSProductResult getProduct(LrsWSRegistrationAuthority ra, String order) {
        return null;
    }

    @Override
    public Response<GetStatusResponse> getStatusAsync(LrsWSRegistrationAuthority ra, String order) {
        LrsWSOrderStatusEnum newStatus = null;
        switch (status.get(order)) {
            case WAITING:
                newStatus = LrsWSOrderStatusEnum.ONGOING;
                break;
            case ONGOING:
                newStatus = LrsWSOrderStatusEnum.PRODUCED;
                break;
            case PRODUCED:
                newStatus = LrsWSOrderStatusEnum.ACTIVATED;
                break;
        }
        LOG.info("moving order {} to status {}", order, newStatus);
        status.put(order, newStatus);
        LrsWSStatusResult result = new LrsWSStatusResult();
        result.setStatus(newStatus);
        GetStatusResponse response = new GetStatusResponse();
        response.setReturn(result);
        return new MockResponse<>(response);
    }

    @Override
    public synchronized Future<?> getStatusAsync(LrsWSRegistrationAuthority ra, String order, AsyncHandler<GetStatusResponse> asyncHandler) {
        asyncHandler.handleResponse(getStatusAsync(ra, order));
        return null;
    }

    @Override
    public LrsWSStatusResult getStatus(LrsWSRegistrationAuthority ra, String order) {
        return null;
    }

    @Override
    public Response<RegisterResponse> registerAsync(LrsWSRegistrationAuthority ra, LrsWSOrder order, LrsWSSignature rao, String version) {
        return null;
    }

    @Override
    public Future<?> registerAsync(LrsWSRegistrationAuthority ra, LrsWSOrder order, LrsWSSignature rao, String version, AsyncHandler<RegisterResponse> asyncHandler) {
        return null;
    }

    @Override
    public synchronized LrsWSRegisterResult register(LrsWSRegistrationAuthority ra, LrsWSOrder order, LrsWSSignature rao, String version) {
        String orderNumber = UUID.randomUUID().toString();
        status.put(orderNumber, LrsWSOrderStatusEnum.WAITING);
        LrsWSRegisterResult lrsWSRegisterResult = new LrsWSRegisterResult();
        if (this.orderId2LrsNumber.containsKey(order.getArchive())) {
            lrsWSRegisterResult.setOrderNumber(orderId2LrsNumber.get(order.getArchive()));
            LOG.info("order {} registered", orderId2LrsNumber.get(order.getArchive()));
        } else {
            lrsWSRegisterResult.setOrderNumber(orderNumber);
            LOG.info("order {} registered", orderNumber);
        }
        return lrsWSRegisterResult;
    }

    public void putLRSNumberForOrder(String orderId, String lrsNumber) {
        this.orderId2LrsNumber.put(orderId, lrsNumber);
    }

    public void removeLrsNumberForOrder(String orderId) {
        this.orderId2LrsNumber.remove(orderId);
    }

    public static class MockResponse<T> implements Response<T> {

        private T result;

        public MockResponse(T result) {
            this.result = result;
        }

        @Override
        public Map<String, Object> getContext() {
            return null;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return null;
        }
    }

    private static class MockProduct extends LrsWSProduct {

        void setCertificates(List<LrsWSCertificate> certificates) {
            this.certificate = certificates;
        }

    }
}
