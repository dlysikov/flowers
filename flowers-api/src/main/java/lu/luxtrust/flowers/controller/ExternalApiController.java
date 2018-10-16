package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.model.UploadedFiles;
import lu.luxtrust.flowers.model.ValidatedList;
import lu.luxtrust.flowers.security.RequestorRestAuthenticationToken;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.ResponseAPIService;
import lu.luxtrust.flowers.service.StaticDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static lu.luxtrust.flowers.validation.ValidationGroups.OrderUserDraftAPI;

@RestController
@RequestMapping("/api/external")
public class ExternalApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalApiController.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private final ResponseAPIService responseAPIService;
    private final StaticDataService staticDataService;
    private final int itemsCount;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null && binder.getTarget().getClass().isAssignableFrom(ValidatedList.class)) {
            ValidatedList<CertificateOrder> orders = (ValidatedList<CertificateOrder>) binder.getTarget();
            staticDataService.enrichWithCountries(orders.getItems().stream().filter(o -> o.getUnit() != null).map(CertificateOrder::getUnit).collect(Collectors.toList()));
            staticDataService.enrichWithCountries(orders.getItems().stream().filter(o -> o.getAddress() != null).map(CertificateOrder::getAddress).collect(Collectors.toList()));
            staticDataService.enrichWithNationalities(orders.getItems().stream().filter(o -> o.getHolder() != null).map(CertificateOrder::getHolder).collect(Collectors.toList()));
        }
    }

    @Autowired
    public ExternalApiController(ResponseAPIService responseAPIService, @Value("${external.api.batch.size:1000}") int itemsCount, StaticDataService staticDataService) {
        this.responseAPIService = responseAPIService;
        this.staticDataService = staticDataService;
        this.itemsCount = itemsCount;
    }

    @PutMapping(value = "/orders")
    public ResponseEntity<List<ResponseAPI>> createOrders(@RequestBody @Validated(OrderUserDraftAPI.class) ValidatedList<CertificateOrder> orderList,
                                                          RequestorRestAuthenticationToken token) {
        if (orderList.getItems().size() > itemsCount) {
            throw new FlowersException("Size to big", "items length must be between 0 and " + itemsCount, "items.length.exceeded", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        List<ResponseAPI> responseAPIList = new ArrayList<>();
        LOG.info(auditMarker, "The processing of [{}] orders by API is starting now", orderList.getItems().size());
        for (CertificateOrder order : orderList.getItems()) {
            responseAPIList.add(responseAPIService.saveOrder(order, token.getPrincipal()));
        }
        LOG.info(auditMarker, "The successful processing of [{}] orders by API was finished", responseAPIList.size());
        return ResponseEntity.ok(responseAPIList);
    }

    @PreAuthorize("isExternalUserIdFromAuthenticatedService(#userExternalId) and areDocumentsCanBeUploadedByAPI(#userExternalId)")
    @PostMapping(value = "/orders/{userExternalId}/document")
    public ResponseEntity<ResponseAPI> saveDocuments(@PathVariable("userExternalId") String userExternalId, @Validated UploadedFiles documents,
                                                     RequestorRestAuthenticationToken token) throws Exception {
        LOG.info(auditMarker, "The loading of {} documents for user external id {} is starting now", documents.getFile().length, userExternalId);
        ResponseAPI responseAPI = responseAPIService.saveDocuments(userExternalId, documents, token.getPrincipal());
        LOG.info(auditMarker, "The loading of {} documents for user external id {} was finished", documents.getFile().length, userExternalId);
        return ResponseEntity.ok(responseAPI);
    }

}
