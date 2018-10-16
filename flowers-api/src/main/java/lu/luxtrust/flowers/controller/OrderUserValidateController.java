package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Document;
import lu.luxtrust.flowers.error.ApiErrors;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.OrderUserPageMobileCodeVerification;
import lu.luxtrust.flowers.model.UploadedFiles;
import lu.luxtrust.flowers.repository.DocumentRepository;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.service.DocumentService;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.validation.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/user/validate")
public class OrderUserValidateController {
    private static final Logger LOG = LoggerFactory.getLogger(OrderUserValidateController.class);

    private final CertificateOrderService certificateOrderService;
    private final CertificateOrderRepository orderRepository;
    private final OrderUserValidatePageRepository orderUserValidatePageRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final Boolean sendSmsValidationCode;
    private final RequiredFieldsResolver resolver;

    @Autowired
    public OrderUserValidateController(CertificateOrderService certificateOrderService,
                                       @Value("${user-validation-page.mobile-code-validation.enabled}") Boolean sendSmsValidationCode,
                                       CertificateOrderRepository orderRepository,
                                       OrderUserValidatePageRepository orderUserValidatePageRepository,
                                       DocumentRepository documentRepository,
                                       DocumentService documentService,
                                       RequiredFieldsResolver resolver) {
        this.sendSmsValidationCode = sendSmsValidationCode;
        this.certificateOrderService = certificateOrderService;
        this.orderRepository = orderRepository;
        this.orderUserValidatePageRepository= orderUserValidatePageRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.resolver = resolver;
    }

    @PreAuthorize("canAccessOrderValidatePage(#pageHash) and hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @PostMapping("/{pageHash}/required_fields")
    public ResponseEntity<ApiErrors> getRequiredFields(@RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode,
                                                       @PathVariable("pageHash") String pageHash,
                                                       @RequestBody CertificateOrder order, BindingResult result) {
        return resolver.getRequiredFields(order, result, ValidationGroups.OrderUserDraft.class);
    }

    @GetMapping("/is_mobile_code_required/{pageHash}")
    public ResponseEntity<Boolean> isMobileValidationCodeRequired(@PathVariable("pageHash") String pageHash) {
        if (orderUserValidatePageRepository.findNotExpiredByPageHash(pageHash) == null) {
            throw new FlowersException("invalid page hash", null, null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sendSmsValidationCode);
    }

    @PreAuthorize("canAccessOrderValidatePage(#pageHash) and hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @PostMapping("/{pageHash}/document")
    public ResponseEntity<List<Document>> uploadDocuments(@RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode,
                                                          @PathVariable("pageHash") String pageHash,
                                                          @Validated UploadedFiles documents) throws IOException {
        Long orderId = orderUserValidatePageRepository.findOrderIdByPageHash(pageHash);
        if (orderId == null) {
            LOG.warn("CertificateOrder is not found for page hash {}", pageHash);
            return ResponseEntity.notFound().build();
        }
        LOG.info("Documents are being uploaded for order with id {}", orderId);
        Map<String, InputStream> fileName2Stream = new HashMap<>();
        for (MultipartFile file: documents.getFile()) {
            if (documentService.isValidOrderDocumentFormat(file)) {
                fileName2Stream.put(file.getOriginalFilename(), file.getInputStream());
            }
        }
        List<Document> result = certificateOrderService.uploadDocumentsToOrder(fileName2Stream, orderId);
        LOG.info("{} documents were successfully uploaded for order with id {}", result.size(), orderId);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("canAccessOrderValidatePage(#pageHash) and hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @GetMapping(value = "/{pageHash}/document/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadDocument(@PathVariable("pageHash") String pageHash,
                                                     @PathVariable("documentId") Long documentId,
                                                     @RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode) {
       return documentService.downloadDocument(documentId);
    }

    @PreAuthorize("canAccessOrderValidatePage(#pageHash) and hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @DeleteMapping("/{pageHash}/document/{documentName:.+}")
    public ResponseEntity deleteDocument(@PathVariable("pageHash") String pageHash,
                                         @PathVariable("documentName") String documentName,
                                         @RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode) {
        Long orderId = orderUserValidatePageRepository.findOrderIdByPageHash(pageHash);
        if (orderId == null) {
            LOG.warn("CertificateOrder is not found for page hash {}", pageHash);
            return ResponseEntity.notFound().build();
        }
        Long holderId = orderRepository.findHolderIdForOrder(orderId);
        LOG.info("Deleting of the document [{}] for order with id {} and holder with id [{}]", documentName, orderId, holderId);
        int rowsCount = certificateOrderService.removeDocumentByName(holderId, documentName);
        LOG.info("[{}] Documents were deleted for order with id {} and holder with id [{}]", rowsCount, orderId, holderId);
        return rowsCount != 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @GetMapping("/{pageHash}")
    public ResponseEntity<CertificateOrder> getUserValidatePage(@PathVariable("pageHash") String pageHash,
                                                                @RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode) {
        CertificateOrder order = certificateOrderService.findOrderToValidateByUser(pageHash);
        if (order == null) {
            LOG.warn("Not existed user validation page with hash {} is being tried to be accessed", pageHash);
        }
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("canAccessOrderValidatePage(#pageHash) and hasValidAuthCodeForOrderValidationPage(#authCode, #pageHash)")
    @PostMapping("/{pageHash}")
    public ResponseEntity<CertificateOrder> validateOrderByUser(@RequestHeader(name = "Order-User-Validates-Auth", required = false) String authCode,
                                                                @PathVariable("pageHash") String pageHash,
                                                                @RequestBody @Validated(ValidationGroups.OrderUserDraft.class) CertificateOrder order,
                                                                BindingResult br) throws Exception {
        if (isOrderWithoutDocuments(order)) {
            br.rejectValue("holder.documents", "NotEmpty");
            throw new BindException(br);
        }
        if (br.hasErrors()) {
            throw new BindException(br);
        }
        CertificateOrder result = certificateOrderService.validateByUser(order);
        LOG.info("CertificateOrder {} has been validated by User", order.getId());
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("canAccessOrderValidatePage(#verification.pageHash)")
    @PostMapping("/verify_mobile_code")
    public ResponseEntity<Boolean> isMobileCodeValid(@RequestBody @Validated OrderUserPageMobileCodeVerification verification) {
        if (orderUserValidatePageRepository.findNotExpiredByPageHash(verification.getPageHash()) == null) {
            throw new FlowersException("invalid page hash", null, null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(this.orderUserValidatePageRepository.findIdByPageHashAndMobileValidationCode(verification.getPageHash(), verification.getMobileCode()) != null);
    }

    private boolean isOrderWithoutDocuments(CertificateOrder order) {
        return documentRepository.countByHolderId(order.getHolder().getId()) == 0
                || order.getHolder().getDocuments() == null
                || order.getHolder().getDocuments().isEmpty();
    }
}
