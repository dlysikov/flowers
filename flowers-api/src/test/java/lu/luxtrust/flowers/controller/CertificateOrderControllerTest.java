package lu.luxtrust.flowers.controller;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.model.UploadedFile;
import lu.luxtrust.flowers.repository.DocumentRepository;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.DocumentService;
import lu.luxtrust.flowers.model.ImportResult;
import lu.luxtrust.flowers.service.OrderBatchService;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.validator.OrdersXMLValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CertificateOrderControllerTest {

    private static final long DOCUMENT_ID = 3L;
    private static final long AUTHENTICATION_ID = 2L;
    private static final long ORDER_ID = 1L;
    private static final long REQUESTOR_ID = 4L;

    @Mock
    private CertificateOrder order;
    @Mock
    private ImportResult<CertificateOrder> result;
    @Mock
    private CertificateOrderService certificateOrderService;
    @Mock
    private Holder holder;
    @Mock
    private OrderBatchService orderBatchService;
    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private OrdersXMLValidator ordersXMLValidator;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private RestAuthenticationToken authentication;
    @Mock
    private UploadedFile uploadXmlFile;
    @Mock
    private Document document;
    @Mock
    private InputStream inputStream;
    @Mock
    private MultipartFile xmlFile;
    @Mock
    private Requestor requestor;
    @Mock
    private PageParams pageParams;
    @Mock
    private Unit unit;
    @Mock
    private ResponseEntity<Resource> documentsResult;
    @Mock
    private RequiredFieldsResolver resolver;

    private CertificateOrderController target;

    @Before
    public void init() throws Exception {
        when(requestor.getId()).thenReturn(REQUESTOR_ID);
        when(order.getHolder()).thenReturn(holder);
        when(uploadXmlFile.getFile()).thenReturn(xmlFile);
        when(xmlFile.getInputStream()).thenReturn(inputStream);
        when(order.getId()).thenReturn(ORDER_ID);
        when(authentication.getId()).thenReturn(AUTHENTICATION_ID);
        when(authentication.getUnit()).thenReturn(unit);
        when(unit.getRequestor()).thenReturn(requestor);
        this.target = new CertificateOrderController(certificateOrderService, ordersXMLValidator, orderRepository, orderBatchService, documentService, resolver);
    }

    @Test
    public void getOrder_NotFound() {
        ResponseEntity<CertificateOrder> response = target.getOrder(ORDER_ID);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(certificateOrderService).findById(ORDER_ID);
    }

    @Test
    public void getOrder() {
        when(certificateOrderService.findById(ORDER_ID)).thenReturn(order);
        ResponseEntity<CertificateOrder> response = target.getOrder(ORDER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(order);
        verify(certificateOrderService).findById(ORDER_ID);
    }

    @Test
    public void getOrderList() {
        PageResponse<CertificateOrder> ordersPage = new PageResponse<>(Lists.newArrayList(order), 1L);
        when(certificateOrderService.findAll(unit, pageParams)).thenReturn(ordersPage);
        ResponseEntity<PageResponse<CertificateOrder>> response = target.getOrderList(authentication, pageParams);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(ordersPage);
        verify(certificateOrderService).findAll(unit, pageParams);
    }

    @Test
    public void createOrder() {
        when(certificateOrderService.saveOrder(order)).thenReturn(order);

        ResponseEntity<CertificateOrder> response = target.createOrder(this.order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(order);

        verify(certificateOrderService).saveOrder(order);
    }

    @Test
    public void submitOrder_IdInObjectNotEqualsToRequest() throws Exception {
        when(order.getId()).thenReturn(2L);
        ResponseEntity response = target.submitOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();

        verify(certificateOrderService, never()).notifyOrderHolder(any(CertificateOrder.class));
    }

    @Test
    public void updateOrder_IdInObjectNotEqualsToRequest() {
        when(order.getId()).thenReturn(2L);
        ResponseEntity response = target.updateOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();

        verify(certificateOrderService, never()).saveOrder(any(CertificateOrder.class));
    }

    @Test
    public void submitOrder_NotExists() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.FALSE);

        ResponseEntity response = target.submitOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(certificateOrderService, never()).notifyOrderHolder(any(CertificateOrder.class));
    }

    @Test
    public void updateOrder_NotExists() {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.FALSE);

        ResponseEntity response = target.updateOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(certificateOrderService, never()).saveOrder(any(CertificateOrder.class));
    }

    @Test
    public void updateOrder() {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.TRUE);

        ResponseEntity response = target.updateOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(certificateOrderService).saveOrder(order);
    }

    @Test
    public void submitOrder() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.TRUE);

        ResponseEntity response = target.submitOrder(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(certificateOrderService).notifyOrderHolder(order);
    }

    @Test
    public void submitOrderWithoutDraft() throws Exception {
        when(certificateOrderService.createAndNotifyOrderHolder(order)).thenReturn(order);

        ResponseEntity<CertificateOrder> response = target.submitOrderWithoutDraft(order, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(order);
        verify(certificateOrderService).createAndNotifyOrderHolder(order);
    }

    @Test
    public void createOrderFromXML() throws Exception {
        when(result.getFailedDetails()).thenReturn(Lists.newArrayList(order));
        when(orderBatchService.processOrdersXML(inputStream, AUTHENTICATION_ID)).thenReturn(result);

        ResponseEntity<ImportResult<CertificateOrder>> response = target.createOrderFromXML(uploadXmlFile, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(orderBatchService).processOrdersXML(inputStream, AUTHENTICATION_ID);
        verify(holder).setDocuments(Collections.emptyList());
    }

    @Test
    public void reject_NotFound() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.FALSE);

        ResponseEntity response = target.reject(ORDER_ID, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
        verify(certificateOrderService, never()).reject(any(CertificateOrder.class));
    }

    @Test
    public void reject() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.TRUE);
        when(certificateOrderService.findById(ORDER_ID)).thenReturn(order);

        ResponseEntity response = target.reject(ORDER_ID, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
        verify(certificateOrderService).reject(order);
    }

    @Test
    public void sendToLrs_IdInRequestNotEqualsToBody() throws Exception {
        ResponseEntity response = target.sendToLrs(2L, order, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(certificateOrderService, never()).sendToLrs(any(CertificateOrder.class));
    }

    @Test
    public void sendToLrs_OrderNotFound() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.FALSE);

        ResponseEntity response = target.sendToLrs(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(certificateOrderService, never()).sendToLrs(any(CertificateOrder.class));
    }

    @Test
    public void sendToLrs() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.TRUE);

        ResponseEntity response = target.sendToLrs(ORDER_ID, order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(certificateOrderService).sendToLrs(order);
    }

    @Test
    public void sendToLrsFromDraft() throws Exception {
        when(orderRepository.exists(ORDER_ID)).thenReturn(Boolean.TRUE);
        when(certificateOrderService.createAndSendToLrs(order)).thenReturn(order);

        ResponseEntity<CertificateOrder> response = target.sendToLrsFromDraft(order, authentication);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(order);
        verify(certificateOrderService).createAndSendToLrs(order);
    }

    @Test
    public void downloadDocument() throws Exception {
        when(documentService.downloadDocument(DOCUMENT_ID)).thenReturn(documentsResult);

        ResponseEntity<Resource> response = this.target.downloadDocument(DOCUMENT_ID, 1L);
        assertThat(response).isSameAs(documentsResult);
    }
}