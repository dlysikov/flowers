package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.integration.ltss.client.LTSSClient;
import lu.luxtrust.flowers.model.ESealCredentials;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.ESealService;
import lu.luxtrust.ltss.dto.PublicKeyDTO;
import lu.luxtrust.ltss.dto.ResultCodeDTO;
import lu.luxtrust.ltss.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@RestController
@RequestMapping("/api/eseal_order")
public class ESealOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(ESealOrderController.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private final ESealService service;
    private final ESealOrderRepository repository;
    private final UserRepository userRepository;
    private LTSSClient ltssClient;

    @Autowired
    public ESealOrderController(ESealService service, ESealOrderRepository repository, UserRepository userRepository, LTSSClient ltssClient) {
        this.service = service;
        this.repository = repository;
        this.userRepository = userRepository;
        this.ltssClient = ltssClient;
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','read') and isManagerOf('ESealOrder', #id)")
    @GetMapping("/{id}")
    public ResponseEntity<ESealOrder> getOrder(RestAuthenticationToken authentication, @PathVariable("id") Long id) {
        LOG.info(auditMarker, "User with id={}, is trying to get data for eSeal order with id={}", authentication.getId(), id);
        ESealOrder order = repository.findOne(id);
        if (order != null) {
            LOG.info(auditMarker, "ESeal order with id={} found for user id={}", id, authentication.getId());
            return ResponseEntity.ok(order);
        } else {
            LOG.info(auditMarker, "ESeal order with id={} doesn't exists. User id={}", id, authentication.getId());
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','read')")
    @GetMapping
    public ResponseEntity<PageResponse<ESealOrder>> getOrderList(RestAuthenticationToken authentication, PageParams params) {
        LOG.info(auditMarker, "Retrieving eSeal orders for user with id={}", authentication.getId());
        List<RoleType> roles = authentication.getAuthorities().stream().map(au -> RoleType.valueOf(au.getAuthority())).collect(Collectors.toList());
        PageResponse<ESealOrder> orders = service.findAll((roles.contains(RoleType.ESEAL_MANAGER) ? authentication.getId() : null), params);
        LOG.info(auditMarker, "Found {} eSeal orders for user with id={}", orders.getData().size(), authentication.getId());
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','read')")
    @GetMapping("/managers/{unit_id}")
    public ResponseEntity<List<User>> eSealManagersForUnit(@PathVariable("unit_id") Long unitId) {
        return ResponseEntity.ok(userRepository.findByRoleAndUnit(RoleType.ESEAL_MANAGER, unitId));
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','read')")
    @GetMapping("/managers")
    public ResponseEntity<List<User>> eSealManagers() {
        return ResponseEntity.ok(userRepository.findByRole(RoleType.ESEAL_MANAGER));
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','read') and isManagerOf('ESealOrder', #id)")
    @GetMapping("/{id}/valuable_data_modifiable")
    public ResponseEntity<Boolean> isModifiable(@PathVariable("id") Long id, RestAuthenticationToken authentication) {
        LOG.info(auditMarker, "Getting modifiable status for eSeal order with id={}, by user with id={}", id, authentication.getId());
        if (repository.exists(id)) {
            boolean modifiable = service.isValuableDataModifiable(id);
            LOG.info(auditMarker, "eSeal order with id={} modifiable status is {}. Request for user id={}", id, modifiable, authentication.getId());
            return ResponseEntity.ok(modifiable);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','edit', 'create') and isManagerOf('ESealOrder', #order)")
    @PostMapping("/draft")
    public ResponseEntity<ESealOrder> saveAsDraft(RestAuthenticationToken authentication, @RequestBody @Validated ESealOrder order) {
        LOG.info(auditMarker, "User with id={} is {} eSeal order draft {}", (order.getId() == null ? "creating a new" : "updating"), authentication.getId(), order);
        service.save(order);
        LOG.info(auditMarker, "User with id={} successfully {} eSeal order draft with id={}", (order.getId() == null ? "created new" : "updated"), authentication.getId(), order.getId());
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','edit', 'create') and isManagerOf('ESealOrder', #order)")
    @PostMapping
    public ResponseEntity<ESealOrder> save(RestAuthenticationToken authentication, @RequestBody @Validated(OrderSentToLRS.class) ESealOrder order) {
        LOG.info(auditMarker, "User with id={} is {} eSeal order {}", (order.getId() == null ? "creating a new" : "updating"), authentication.getId(), order);
        service.save(order);
        LOG.info(auditMarker, "User with id={} successfully {} eSeal order with id={}", (order.getId() == null ? "created new" : "updated"), authentication.getId(), order.getId());
        return ResponseEntity.ok(order);
    }

    @PreAuthorize("hasAnyPermission('ESealOrder','sign_and_send') and isManagerOf('ESealOrder', #order)")
    @PostMapping("/send_to_lrs")
    public ResponseEntity<ESealOrder> sendToLRS(RestAuthenticationToken authentication, @RequestBody @Validated(OrderSentToLRS.class) ESealOrder order) throws Exception {
        LOG.info(auditMarker, "User with id={} is sending eSeal order with id={} to LRS", authentication.getId(), order);
        service.sendToLrs(order);
        LOG.info(auditMarker, "User with id={} successfully sent eSeal order with id={} to LRS", authentication.getId(), order.getId());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/activate/keys")
    public ResponseEntity<PublicKeyDTO> getKeys() {
        LOG.info("Getting Public Key from LTSS Client");
        PublicKeyDTO publicKeyDTO = ltssClient.generateKeys();
        LOG.info("Result of Getting Public Key is {}", publicKeyDTO.getResult().getResultCode());
        return  ResponseEntity.ok(publicKeyDTO);
    }

    @PreAuthorize("hasAnyPermission('ESealOrder', 'activate')")
    @PostMapping("/activate")
    public ResponseEntity<ResultDTO> activate(@RequestBody @Validated ESealCredentials eSealCredentials) {
        LOG.info("Activation eSeal for sealId: {}", eSealCredentials.getSealId());

        ResultDTO resultDTO = ltssClient.activateESealAccount(eSealCredentials);
        LOG.info("Result of Activation eSeal Account [{}] is  [ resultCode = {}, message = {}, additionalInfo = {}]",
                eSealCredentials.getSealId(), resultDTO.getResultCode(), resultDTO.getMessage(), resultDTO.getAdditionalInfo());

        if (!resultDTO.getResultCode().equals(ResultCodeDTO.OK)) {
            throw new FlowersException(resultDTO.getMessage(), resultDTO, resultDTO.getResultCode().getCode());
        }

        resultDTO = ltssClient.destroyKeys(eSealCredentials.getKeyId());
        LOG.info("Result of Destroying key for sealId [{}] is  [ resultCode = {}, message = {}, additionalInfo = {}]",
                eSealCredentials.getSealId(), resultDTO.getResultCode(), resultDTO.getMessage(), resultDTO.getAdditionalInfo());

        if (!resultDTO.getResultCode().equals(ResultCodeDTO.OK)) {
            throw new FlowersException(resultDTO.getMessage(), resultDTO, resultDTO.getResultCode().getCode());
        }
        return ResponseEntity.ok(resultDTO);
    }

}
