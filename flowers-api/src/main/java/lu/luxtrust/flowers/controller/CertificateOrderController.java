package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.error.ApiErrors;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.model.UploadedFile;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.DocumentService;
import lu.luxtrust.flowers.model.ImportResult;
import lu.luxtrust.flowers.service.OrderBatchService;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.validation.ValidationGroups;
import lu.luxtrust.flowers.validator.OrdersXMLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@RestController
@RequestMapping("/api/orders")
public class CertificateOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateOrderController.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private final CertificateOrderService certificateOrderService;
    private final OrderBatchService orderBatchService;
    private final CertificateOrderRepository orderRepository;
    private final OrdersXMLValidator ordersXMLValidator;
    private final DocumentService documentService;
    private final RequiredFieldsResolver resolver;

    @Autowired
    public CertificateOrderController(CertificateOrderService certificateOrderService,
                                      OrdersXMLValidator ordersXMLValidator,
                                      CertificateOrderRepository orderRepository,
                                      OrderBatchService orderBatchService,
                                      DocumentService documentService,
                                      RequiredFieldsResolver resolver) {
        this.certificateOrderService = certificateOrderService;
        this.ordersXMLValidator = ordersXMLValidator;
        this.orderBatchService = orderBatchService;
        this.orderRepository = orderRepository;
        this.documentService = documentService;
        this.resolver = resolver;
    }

    @InitBinder
    protected void initBinderFileBucket(WebDataBinder binder, RestAuthenticationToken authentication) {
        if (binder.getTarget() != null && this.ordersXMLValidator.supports(binder.getTarget().getClass())) {
            binder.setValidator(ordersXMLValidator);
        }
        if (binder.getTarget() != null && binder.getTarget().getClass().isAssignableFrom(CertificateOrder.class)) {
            CertificateOrder order = (CertificateOrder) binder.getTarget();
            if (order.getId() == null) {
                order.setIssuer(new User(authentication.getId()));
            }
        }
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','read') and isManagerOf('CertificateOrder', #id)")
    @GetMapping("/{id}")
    public ResponseEntity<CertificateOrder> getOrder(@PathVariable("id") Long id) {
        LOG.info(auditMarker, "Retrieving order with id {}", id);
        CertificateOrder order = certificateOrderService.findById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','read')")
    @GetMapping
    public ResponseEntity<PageResponse<CertificateOrder>> getOrderList(RestAuthenticationToken authentication, PageParams params) {
        LOG.info(auditMarker, "Retrieving certificate orders for user with id={}", authentication.getId());
        PageResponse<CertificateOrder> orders = certificateOrderService.findAll(authentication.getUnit(), params);
        LOG.info(auditMarker, "Found {} certificate orders for user with id={}", orders.getData().size(), authentication.getId());
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder', 'create') and isCreatingNew(#order)")
    @PutMapping
    public ResponseEntity<CertificateOrder> createOrder(@Validated @RequestBody CertificateOrder order, RestAuthenticationToken authentication) {
        LOG.info(auditMarker, "User with id {} is creating a new order", authentication.getId());
        CertificateOrder result = certificateOrderService.saveOrder(order);
        LOG.info(auditMarker, "User with id {} created a new order with id {}", authentication.getId(), result.getId());
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder', 'read') and isManagerOf('CertificateOrder', #order)")
    @PostMapping("/required_fields")
    public ResponseEntity<ApiErrors> getRequiredFields(@RequestBody CertificateOrder order, BindingResult result, RestAuthenticationToken authentication) {
        switch (order.getStatus()) {
            case DRAFT:
                if (shortFlow(authentication)) {
                    return resolver.getRequiredFields(order, result, OrderSentToLRS.class, "tokenSerialNumber");
                } else {
                    return resolver.getRequiredFields(order, result, OrderDraft.class);
                }
            case REMOTE_IDENTIFICATION_REQUIRED:
            case FACE_2_FACE_REQUIRED:
            case DIA_SIGNING_REQUIRED:
            case CSD_REVIEW_REQUIRED:
                return resolver.getRequiredFields(order, result, OrderSentToLRS.class);
            default:
                return ResponseEntity.ok(new ApiErrors(Collections.emptySet(), Collections.emptySet()));
        }
    }

    private boolean shortFlow(RestAuthenticationToken authentication) {
        return authentication.getUnit() != null && authentication.getUnit().getRequestor().getConfig().getShortFlow();
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder', 'edit') and isOrderInStatus(#order, 'DRAFT','') and isManagerOf('CertificateOrder', #order)")
    @PostMapping("/{id}")
    public ResponseEntity<CertificateOrder> updateOrder(@PathVariable("id") Long orderId, @Validated @RequestBody CertificateOrder order, RestAuthenticationToken authentication) {
        if (!orderId.equals(order.getId())) {
            LOG.warn(auditMarker, "User with id {} is trying to update order with id {}, when real id is {}", authentication.getId(), orderId, order.getId());
            return ResponseEntity.badRequest().build();
        }
        if (!orderRepository.exists(orderId)) {
            LOG.warn(auditMarker, "CertificateOrder with id {} does not exist", orderId);
            return ResponseEntity.notFound().build();
        }
        LOG.info(auditMarker, "User with id {} is updating order {}", authentication.getId(), order.getId());
        return ResponseEntity.ok(certificateOrderService.saveOrder(order));
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder', 'edit') and isOrderInStatus(#order, 'DRAFT') and isManagerOf('CertificateOrder', #order)")
    @PostMapping("/{id}/submit")
    public ResponseEntity submitOrder(@PathVariable("id") Long orderId, @Validated(OrderDraft.class) @RequestBody CertificateOrder order, RestAuthenticationToken authentication) throws Exception {
        if (!orderId.equals(order.getId())) {
            LOG.warn(auditMarker, "User with id {} is trying to move order with id {} to User Draft status, when real id is {}", authentication.getId(), order, order.getId());
            return ResponseEntity.badRequest().build();
        }
        if (!orderRepository.exists(orderId)) {
            LOG.warn(auditMarker, "CertificateOrder with id {} does not exist", orderId);
            return ResponseEntity.notFound().build();
        }
        LOG.info(auditMarker, "User {} is trying to move CertificateOrder {} to User Draft status", authentication.getId(), orderId);
        certificateOrderService.notifyOrderHolder(order);
        LOG.info(auditMarker, "User {} has moved CertificateOrder {} to User Draft status", authentication.getId(), orderId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder', 'edit') and isCreatingNew(#order)")
    @PutMapping("/submit")
    public ResponseEntity<CertificateOrder> submitOrderWithoutDraft(@Validated(OrderDraft.class) @RequestBody CertificateOrder order, RestAuthenticationToken authentication) throws Exception {
        LOG.info(auditMarker, "User with id {} is trying to create a new order and move it to User Draft status", authentication.getId());
        order = certificateOrderService.createAndNotifyOrderHolder(order);
        LOG.info(auditMarker, "User {} has created a new order with id {} and moved it to User Draft status", authentication.getId(), order.getId());
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','create_in_batch')")
    @PostMapping("/xml")
    public ResponseEntity<ImportResult<CertificateOrder>> createOrderFromXML(@Valid UploadedFile ordersXML, RestAuthenticationToken authentication) throws Exception {
        ImportResult<CertificateOrder> result = orderBatchService.processOrdersXML(ordersXML.getFile().getInputStream(), authentication.getId());
        result.getFailedDetails().forEach(o -> o.getHolder().setDocuments(Collections.emptyList()));
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','read') and isManagerOf('CertificateOrder', #orderId)")
    @GetMapping(value = "/{id}/document/{documentId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") Long documentId, @PathVariable("id") Long orderId) {
        return documentService.downloadDocument(documentId);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','sign_and_send') and (isOrderInStatus(#order, 'REMOTE_IDENTIFICATION_REQUIRED', 'FACE_2_FACE_REQUIRED','CSD_REVIEW_REQUIRED', 'DIA_SIGNING_REQUIRED') or isShortEnrollment(#order.id)) and isManagerOf('CertificateOrder', #order)")
    @PostMapping("/{id}/send_to_lrs")
    public ResponseEntity<CertificateOrder> sendToLrs(@PathVariable("id") Long orderId, @Validated(ValidationGroups.OrderSentToLRS.class) @RequestBody CertificateOrder order, RestAuthenticationToken authentication) throws Exception {
        if (!orderId.equals(order.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!orderRepository.exists(orderId)) {
            return ResponseEntity.notFound().build();
        }
        certificateOrderService.sendToLrs(order);
        LOG.info(auditMarker, "CertificateOrder with id {} has been sent to LRS by User {}", order.getId(), authentication.getId());
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','sign_and_send') and isShortEnrollment(#order.id) and isCreatingNew(#order)")
    @PutMapping("/send_to_lrs")
    public ResponseEntity<CertificateOrder> sendToLrsFromDraft(@Validated(ValidationGroups.OrderSentToLRS.class) @RequestBody CertificateOrder order, RestAuthenticationToken authentication) throws Exception {
        LOG.info(auditMarker, "User with id {} is trying to create a new order and send it to LRS", authentication.getId());
        order = certificateOrderService.createAndSendToLrs(order);
        LOG.info(auditMarker, "CertificateOrder with id {} has been sent to LRS by User {}", order.getId(), authentication.getId());
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyPermission('CertificateOrder','edit') and isOrderNotInStatus(#id, 'SENT_TO_LRS', 'PENDING_FINAL_IDENTIFICATION','PENDING_CSD_DECISION','REJECTED') and isManagerOf('CertificateOrder', #id)")
    @PostMapping("{id}/reject")
    public ResponseEntity reject(@PathVariable Long id, RestAuthenticationToken authentication) throws Exception {
        if (!orderRepository.exists(id)) {
            return ResponseEntity.notFound().build();
        }
        certificateOrderService.reject(certificateOrderService.findById(id));
        LOG.info(auditMarker, "CertificateOrder with id {} has been Rejected by User {}", id, authentication.getId());
        return ResponseEntity.ok().build();
    }

}
