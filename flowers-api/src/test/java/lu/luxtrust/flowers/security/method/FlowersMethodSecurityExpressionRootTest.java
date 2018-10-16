package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.*;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.repository.*;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import lu.luxtrust.flowers.service.ResponseAPIService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.GrantedAuthority;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FlowersMethodSecurityExpressionRootTest {

    public static final long REQUESTOR_ID = 1L;
    @Mock
    private RequestorRestAuthenticationToken requestoreToken;
    @Mock
    private RestAuthenticationToken token;
    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private Requestor requestor;
    @Mock
    private Unit unit;
    @Mock
    private Requestor anotherRequestor;
    @Mock
    private Unit anotherUnit;
    @Mock
    private OrderUserValidatePageRepository orderUserValidatePageRepository;
    @Mock
    private RequestorConfiguration config;
    @Mock
    private ResponseAPIService responseAPIService;
    @Mock
    private ResponseAPI responseAPI;
    @Mock
    private GrantedAuthority flowersAdmin;
    @Mock
    private IsManagerOfEvaluator isManagerOfEvaluator;
    @Mock
    private PermissionEvaluator permissionEvaluator;

    private FlowersMethodSecurityExpressionRoot target;

    @InjectMocks
    private MethodSecurityContext context = spy(new MethodSecurityContext());

    @Before
    public void init() {
        when(requestor.getConfig()).thenReturn(config);
        when(requestor.getId()).thenReturn(REQUESTOR_ID);
        when(unit.getId()).thenReturn(1L);
        when(unit.getRequestor()).thenReturn(requestor);
        when(anotherRequestor.getId()).thenReturn(2L);
        when(anotherUnit.getId()).thenReturn(2L);
        when(flowersAdmin.getAuthority()).thenReturn("FLOWERS_ADMIN");
        this.target = new FlowersMethodSecurityExpressionRoot(token, context);
        this.target.setPermissionEvaluator(permissionEvaluator);
    }

    @Test
    public void isManageOfById() {
        doReturn(isManagerOfEvaluator).when(context).getIsManagerOfEvaluator("User");
        when(isManagerOfEvaluator.isManagerOf(token, 1L)).thenReturn(Boolean.TRUE);

        assertThat(target.isManagerOf("User", 1L)).isTrue();
        verify(context).getIsManagerOfEvaluator("User");
        verify(isManagerOfEvaluator).isManagerOf(token, 1L);
    }

    @Test
    public void isManageOfByObject() {
        User u = new User();
        doReturn(isManagerOfEvaluator).when(context).getIsManagerOfEvaluator("User");
        when(isManagerOfEvaluator.isManagerOf(token, u)).thenReturn(Boolean.TRUE);

        assertThat(target.isManagerOf("User", u)).isTrue();
        verify(context).getIsManagerOfEvaluator("User");
        verify(isManagerOfEvaluator).isManagerOf(token, u);
    }

    @Test
    public void setFilterObject() {
        String filterObject = "aa";
        this.target.setFilterObject(filterObject);

        assertThat(this.target.getFilterObject()).isEqualTo(filterObject);
    }

    @Test
    public void setResultObject() {
        String returnObject = "bb";
        this.target.setReturnObject(returnObject);

        assertThat(this.target.getReturnObject()).isEqualTo(returnObject);
    }

    @Test
    public void isEqualToAuthentication() {
        Long id = 1L;
        when(token.getId()).thenReturn(id);

        assertThat(this.target.isEqualToAuthentication(id)).isTrue();
    }

    @Test
    public void isNotEqualToAuthentication() {
        Long id = 1L;
        when(token.getId()).thenReturn(2L);

        assertThat(this.target.isEqualToAuthentication(id)).isFalse();
    }

    @Test
    public void isNewOrderInStatus() {
        assertThat(this.target.isOrderInStatus(new CertificateOrder(), OrderStatus.DRAFT, null)).isTrue();
    }

    @Test
    public void isOrderInStatus() {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().id(1L).build();
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.DRAFT);

        assertThat(this.target.isOrderInStatus(o, OrderStatus.DRAFT, null)).isTrue();
    }

    @Test
    public void isOrderNotInStatus() {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().id(1L).build();
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.USER_DRAFT);

        assertThat(this.target.isOrderInStatus(o, OrderStatus.DRAFT, null)).isFalse();
    }

    @Test
    public void canAccessOrderValidatePage_NotFoundPage() {
        assertThat(this.target.canAccessOrderValidatePage("some-hash")).isTrue();
    }

    @Test
    public void canAccessOrderValidatePageWrongOrderStatus() {
        when(orderUserValidatePageRepository.findOrderStatusForNotExpiredPageHash("aaaaaa")).thenReturn(OrderStatus.FACE_2_FACE_REQUIRED);

        assertThat(this.target.canAccessOrderValidatePage("aaaaaa")).isFalse();
    }

    @Test
    public void canAccessOrderValidatePage() {
        when(orderUserValidatePageRepository.findOrderStatusForNotExpiredPageHash("aaaaaa")).thenReturn(OrderStatus.USER_DRAFT);
        assertThat(this.target.canAccessOrderValidatePage("aaaaaa")).isTrue();
    }

    @Test
    public void isCreatingNewIsTrue() {
        assertThat(target.isCreatingNew(new CertificateOrder())).isTrue();
    }

    @Test
    public void isCreatingNewIsFalse() {
        CertificateOrder order = CertificateOrderBuilder.newBuilder().id(1L).build();
        assertThat(target.isCreatingNew(order)).isFalse();
    }

    @Test
    public void isOrderNotInStatusIsTrue() {
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.USER_DRAFT);
        assertThat(target.isOrderNotInStatus(1L, OrderStatus.DRAFT, OrderStatus.REJECTED)).isTrue();
    }

    @Test
    public void isOrderNotInStatusIsFalse() {
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.DRAFT);
        assertThat(target.isOrderNotInStatus(1L, OrderStatus.DRAFT, OrderStatus.REJECTED)).isFalse();
    }

    @Test
    public void notShortEnrollmentWhenNoRequestor() {
        assertThat(target.isShortEnrollment(1L)).isFalse();
        verify(orderRepository, never()).findOrderStatusById(anyLong());
    }

    @Test
    public void notShortEnrollment() {
        when(token.getUnit()).thenReturn(unit);
        assertThat(target.isShortEnrollment(1L)).isFalse();
        verify(orderRepository, never()).findOrderStatusById(anyLong());
    }

    @Test
    public void notShortEnrollmentWrongOrderStatus() {
        when(token.getUnit()).thenReturn(unit);
        when(config.getShortFlow()).thenReturn(Boolean.TRUE);
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.USER_DRAFT);

        assertThat(target.isShortEnrollment(1L)).isFalse();
        verify(orderRepository).findOrderStatusById(1L);
    }

    @Test
    public void shortEnrollment() {
        when(orderRepository.findOrderStatusById(1L)).thenReturn(OrderStatus.DRAFT);
        when(token.getUnit()).thenReturn(unit);
        when(config.getShortFlow()).thenReturn(Boolean.TRUE);

        assertThat(target.isShortEnrollment(1L)).isTrue();
        verify(orderRepository).findOrderStatusById(1L);
    }

    @Test
    public void areDocumentsCanBeUploadedByAPI_NoAppropriateOrder() {
        String externalUserId = "aaa";
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);
        when(requestoreToken.getPrincipal()).thenReturn(requestor);

        assertThat(target.areDocumentsCanBeUploadedByAPI(externalUserId)).isFalse();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, requestor);
    }

    @Test
    public void areDocumentsCanBeUploadedByAPI_NotInDraftStatus() {
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);
        when(requestoreToken.getPrincipal()).thenReturn(requestor);
        String externalUserId = "aaa";
        when(responseAPIService.findByUserExternalIdAndRequestor(externalUserId, requestor)).thenReturn(responseAPI);
        assertThat(target.areDocumentsCanBeUploadedByAPI(externalUserId)).isFalse();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, requestor);
    }

    @Test
    public void areDocumentsCanBeUploadedByAPI_ShouldNotBeDocuments() {
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);

        String externalUserId = "aaa";
        when(requestoreToken.getPrincipal()).thenReturn(requestor);

        when(responseAPIService.findByUserExternalIdAndRequestor(externalUserId, requestor)).thenReturn(responseAPI);
        assertThat(target.areDocumentsCanBeUploadedByAPI(externalUserId)).isFalse();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, requestor);
    }

    @Test
    public void areDocumentsCanBeUploadedByAPI() {
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);
        when(requestoreToken.getPrincipal()).thenReturn(requestor);
        String externalUserId = "aaa";
        when(responseAPI.getStatus()).thenReturn(StatusAPI.WAITING_FOR_DOCUMENTS);
        when(responseAPIService.findByUserExternalIdAndRequestor(externalUserId, requestor)).thenReturn(responseAPI);
        assertThat(target.areDocumentsCanBeUploadedByAPI(externalUserId)).isTrue();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, requestor);
    }

    @Test
    public void isExternalUserIdFromAuthenticatedServiceNoResponse() {
        String externalUserId = "aaa";
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);
        when(requestoreToken.getPrincipal()).thenReturn(requestor);

        assertThat(target.isExternalUserIdFromAuthenticatedService(externalUserId)).isFalse();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, requestor);
    }

    @Test
    public void isExternalUserIdFromAuthenticatedService_WrongRequestorSSN() {
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);

        String externalUserId = "aaa";
        Requestor si = RequestorBuilder.newBuilder().ssn("1").build();
        ResponseAPI responseAPI = ResponseApiBuilder.newBuilder().serviceSSN("2").build();

        when(requestoreToken.getPrincipal()).thenReturn(si);

        when(responseAPIService.findByUserExternalIdAndRequestor(externalUserId, si)).thenReturn(responseAPI);
        assertThat(target.isExternalUserIdFromAuthenticatedService(externalUserId)).isFalse();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, si);
    }

    @Test
    public void isExternalUserIdFromAuthenticatedService() {
        this.target = new FlowersMethodSecurityExpressionRoot(requestoreToken, context);

        String externalUserId = "aaa";
        Requestor si = RequestorBuilder.newBuilder().ssn("1").build();
        ResponseAPI responseAPI = ResponseApiBuilder.newBuilder().serviceSSN("1").build();
        when(requestoreToken.getPrincipal()).thenReturn(si);

        when(responseAPIService.findByUserExternalIdAndRequestor(externalUserId, si)).thenReturn(responseAPI);
        assertThat(target.isExternalUserIdFromAuthenticatedService(externalUserId)).isTrue();
        verify(responseAPIService).findByUserExternalIdAndRequestor(externalUserId, si);
    }

    @Test
    public void hasValidAuthCodeForOrderValidationPageSmsDisabled() {
        context.setSendSmsValidationCode(Boolean.FALSE);
        assertThat(target.hasValidAuthCodeForOrderValidationPage("afasfasf", "SDFSDF")).isTrue();
    }

    @Test
    public void hasValidAuthCodeForOrderValidationPageSmsEnabledAndCodeValid() {
        context.setSendSmsValidationCode(Boolean.TRUE);
        when(orderUserValidatePageRepository.findIdByPageHashAndMobileValidationCode("AAA","BBB")).thenReturn(1L);
        assertThat(target.hasValidAuthCodeForOrderValidationPage("BBB", "AAA")).isTrue();
    }

    @Test
    public void hasValidAuthCodeForOrderValidationPageSmsEnabledAndCodeInValid() {
        context.setSendSmsValidationCode(Boolean.TRUE);
        when(orderUserValidatePageRepository.findIdByPageHashAndMobileValidationCode("AAA","BBB")).thenReturn(null);
        assertThat(target.hasValidAuthCodeForOrderValidationPage("BBB", "AAA")).isFalse();
    }

    @Test
    public void hasAnyPermissionByTypeTrue() {
        when(permissionEvaluator.hasPermission(token, 0L, "User", "read")).thenReturn(Boolean.TRUE);
        assertThat(target.hasAnyPermission("User", "create", "read")).isTrue();
        verify(permissionEvaluator).hasPermission(token, 0L, "User", "read");
        verify(permissionEvaluator).hasPermission(token, 0L, "User", "create");
    }

    @Test
    public void hasAnyPermissionByTypeFalse() {
        when(permissionEvaluator.hasPermission(token, 0L, "User", "read")).thenReturn(Boolean.TRUE);
        assertThat(target.hasAnyPermission("User", "create", "edit")).isFalse();
        verify(permissionEvaluator).hasPermission(token, 0L, "User", "edit");
        verify(permissionEvaluator).hasPermission(token, 0L, "User", "create");
    }
}