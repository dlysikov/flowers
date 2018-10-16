package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Document;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.model.UploadedFiles;
import lu.luxtrust.flowers.repository.DocumentRepository;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.service.DocumentService;
import lu.luxtrust.flowers.service.CertificateOrderService;
import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderUserValidateControllerTest {

    private static final Long ORDER_ID = 2L;
    private static final Long HOLDER_ID = 1L;
    private static final String PAGE_HASH = "aaaa";
    private static final String AUTH_CODE = "aaab";
    private static final String FILE_NAME = "FILE_NAME";

    @Mock
    private CertificateOrderService certificateOrderService;
    @Mock
    private CertificateOrderRepository orderRepository;
    @Mock
    private OrderUserValidatePageRepository orderUserValidatePageRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private DocumentService documentService;
    @Mock
    private CertificateOrder order;
    @Mock
    private Holder holder;
    @Mock
    private MultipartFile file;
    private MultipartFile[] files;
    @Mock
    private UploadedFiles uploadedFiles;
    @Mock
    private List<Document> documents;
    @Mock
    private InputStream inputStream;
    @Mock
    private BindingResult br;
    @Mock
    private RequiredFieldsResolver resolver;

    private OrderUserValidateController target;

    @Before
    public void init() throws Exception {
        this.files = new MultipartFile[]{file};
        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(file.getInputStream()).thenReturn(inputStream);
        when(uploadedFiles.getFile()).thenReturn(files);
        when(order.getHolder()).thenReturn(holder);
        when(holder.getId()).thenReturn(HOLDER_ID);
        when(holder.getDocuments()).thenReturn(documents);

        this.target = new OrderUserValidateController(certificateOrderService, Boolean.FALSE, orderRepository, orderUserValidatePageRepository, documentRepository, documentService, resolver);
    }

    @Test
    public void getUserValidatePage_NotFound() {
        ResponseEntity<CertificateOrder> response = target.getUserValidatePage(PAGE_HASH, AUTH_CODE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void getUserValidationPage() {
        when(certificateOrderService.findOrderToValidateByUser(PAGE_HASH)).thenReturn(order);

        ResponseEntity<CertificateOrder> response = target.getUserValidatePage(PAGE_HASH, AUTH_CODE);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(order);
    }

    @Test(expected = BindException.class)
    public void validateOrderByUser_NoDocuments() throws Exception {
        when(documentRepository.countByHolderId(HOLDER_ID)).thenReturn(0L);
        target.validateOrderByUser(AUTH_CODE, PAGE_HASH, order, br);
    }

    @Test(expected = BindException.class)
    public void validateOrderByUser_NoDocuments2() throws Exception {
        when(documentRepository.countByHolderId(HOLDER_ID)).thenReturn(1L);
        when(documents.isEmpty()).thenReturn(Boolean.TRUE);
        target.validateOrderByUser(AUTH_CODE, PAGE_HASH, order, br);
    }

    @Test
    public void validateOrderByUser() throws Exception {
        when(documents.isEmpty()).thenReturn(Boolean.FALSE);
        when(documentRepository.countByHolderId(HOLDER_ID)).thenReturn(1L);
        when(certificateOrderService.validateByUser(order)).thenReturn(order);

        ResponseEntity<CertificateOrder> response = target.validateOrderByUser(AUTH_CODE, PAGE_HASH, order, br);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(order);

        verify(br, never()).reject(anyString(), anyString());
    }

    @Test
    public void uploadDocuments_PageNotFound() throws Exception {
        when(orderUserValidatePageRepository.findOrderIdByPageHash(PAGE_HASH)).thenReturn(null);

        ResponseEntity<List<Document>> response = target.uploadDocuments(AUTH_CODE, PAGE_HASH, uploadedFiles);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void uploadDocuments() throws Exception {
        when(orderUserValidatePageRepository.findOrderIdByPageHash(PAGE_HASH)).thenReturn(ORDER_ID);
        when(certificateOrderService.uploadDocumentsToOrder(any(Map.class), anyLong())).thenReturn(documents);
        when(documentService.isValidOrderDocumentFormat(file)).thenReturn(Boolean.TRUE);

        ResponseEntity<List<Document>> response = target.uploadDocuments(AUTH_CODE, PAGE_HASH, uploadedFiles);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(documents);

        ArgumentCaptor<Map> fileCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);

        verify(certificateOrderService).uploadDocumentsToOrder(fileCaptor.capture(), orderIdCaptor.capture());
        assertThat(orderIdCaptor.getValue()).isEqualTo(ORDER_ID);
        assertThat(fileCaptor.getValue()).isEqualTo(Maps.newHashMap(FILE_NAME, inputStream));
    }
}