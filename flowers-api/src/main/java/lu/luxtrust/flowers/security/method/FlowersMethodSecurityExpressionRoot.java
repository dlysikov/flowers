package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.entity.common.ID;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

public class FlowersMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final MethodSecurityContext methodSecurityContext;

    private Object filterObject;
    private Object returnObject;

    FlowersMethodSecurityExpressionRoot(Authentication authentication, MethodSecurityContext methodSecurityContext) {
        super(authentication);
        this.methodSecurityContext = methodSecurityContext;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    public boolean isExternalUserIdFromAuthenticatedService(String externalUserId) {
        RequestorRestAuthenticationToken authentication = (RequestorRestAuthenticationToken) getAuthentication();
        ResponseAPI responseAPI = methodSecurityContext.getResponseAPIService().findByUserExternalIdAndRequestor(externalUserId, authentication.getPrincipal());
        return responseAPI != null && authentication.getPrincipal().getCsn().equals(responseAPI.getServiceSSN());
    }

    public boolean areDocumentsCanBeUploadedByAPI(String userExternalId) {
        RequestorRestAuthenticationToken authentication = (RequestorRestAuthenticationToken) getAuthentication();
        ResponseAPI responseAPI = methodSecurityContext.getResponseAPIService().findByUserExternalIdAndRequestor(userExternalId, authentication.getPrincipal());
        return responseAPI != null && responseAPI.getStatus() == StatusAPI.WAITING_FOR_DOCUMENTS;
    }

    public boolean isEqualToAuthentication(Long orderId) {
        RestAuthenticationToken authentication = (RestAuthenticationToken) getAuthentication();
        return authentication.getId().equals(orderId);
    }

    public boolean isManagerOf(String type, Long id) {
        return methodSecurityContext.getIsManagerOfEvaluator(type).isManagerOf(getAuthentication(), id);
    }

    public boolean isManagerOf(String type, Object targetObject) {
        return methodSecurityContext.getIsManagerOfEvaluator(type).isManagerOf(getAuthentication(), targetObject);
    }

    public <T> boolean isCreatingNew(ID<T> entity) {
        return entity.getId() == null;
    }

    public boolean isOrderInStatus(CertificateOrder order, OrderStatus ... statuses) {
        List<OrderStatus> statusList = Arrays.asList(statuses);
        return statusList.contains(methodSecurityContext.getCertificateOrderRepository().findOrderStatusById(order.getId()));
    }

    public boolean isOrderNotInStatus(Long orderId, OrderStatus ... statuses) {
        return !Arrays.asList(statuses).contains(methodSecurityContext.getCertificateOrderRepository().findOrderStatusById(orderId));
    }

    public boolean canAccessOrderValidatePage(String pageHash) {
        OrderStatus status = methodSecurityContext.getOrderUserValidatePageRepository().findOrderStatusForNotExpiredPageHash(pageHash);
        return status == null || OrderStatus.USER_DRAFT.equals(status);
    }

    public boolean hasValidAuthCodeForOrderValidationPage(String authCode, String pageHash) {
        return !methodSecurityContext.getSendSmsValidationCode() || methodSecurityContext.getOrderUserValidatePageRepository().findIdByPageHashAndMobileValidationCode(pageHash, authCode) != null;
    }

    public boolean isShortEnrollment(Long orderId) {
        RestAuthenticationToken authentication = (RestAuthenticationToken) getAuthentication();
        return authentication.getUnit() != null
                && authentication.getUnit().getRequestor().getConfig().getShortFlow()
                && Arrays.asList(OrderStatus.DRAFT, null).contains(methodSecurityContext.getCertificateOrderRepository().findOrderStatusById(orderId));
    }

    public boolean hasAnyPermission(String targetType, Object ... permissions) {
        for (Object permission: permissions) {
            if (hasPermission(0L, targetType, permission)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
