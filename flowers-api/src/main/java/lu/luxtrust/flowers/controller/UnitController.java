package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.error.GlobalErrorCodes;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    private static final Logger LOG = LoggerFactory.getLogger(UnitController.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private final UnitService unitService;
    private final UnitRepository unitRepository;

    @Autowired
    public UnitController(UnitService unitService, UnitRepository unitRepository) {
        this.unitService = unitService;
        this.unitRepository = unitRepository;
    }

    @PreAuthorize("hasAnyPermission('Unit', 'read')")
    @GetMapping
    public ResponseEntity<PageResponse<Unit>> findAllForAuthenticated(RestAuthenticationToken authentication, PageParams params) {
        LOG.info(auditMarker, "Retrieving units for user with id={}", authentication.getId());
        List<Unit> units = unitService.findAll(params);
        LOG.info(auditMarker, "Found {} units for user with id={}", units.size(), authentication.getId());
        return ResponseEntity.ok(new PageResponse<>(units, unitService.count(params.getFilter())));
    }

    @PreAuthorize("hasAnyPermission('Unit', 'create')")
    @PutMapping
    public ResponseEntity<Unit> createUnit(@RequestBody @Validated Unit unit, RestAuthenticationToken authentication) {
        if (unitRepository.existsByIdentifierTypeAndIdentifierValue(unit.getIdentifier().getType(), unit.getIdentifier().getValue())) {
            LOG.info(auditMarker, "Unit with identifier type={} and identifier={} already exists", unit.getIdentifier().getType(), unit.getIdentifier().getValue());
            throw new FlowersException(String.format("Unit with identifier type=%s and identifier=%s already exists", unit.getIdentifier().getType(), unit.getIdentifier().getValue()),
                                       null,
                                       GlobalErrorCodes.DUPLICATE,
                                       HttpStatus.UNPROCESSABLE_ENTITY);
        }
        LOG.info(auditMarker, "User with id={} is creating a new unit {}", authentication.getId(), unit);
        unit = unitRepository.save(unit);
        LOG.info(auditMarker, "User with id={} successfully created new unit with id={}", authentication.getId(), unit.getId());
        return ResponseEntity.ok(unit);
    }

    @PreAuthorize("hasAnyPermission('Unit', 'edit')")
    @GetMapping("/{id}/valuable_data_modifiable")
    public ResponseEntity<Boolean> isModifiable(@PathVariable("id") Long unitId, RestAuthenticationToken authentication) {
        LOG.info(auditMarker, "Getting modifiable status for unit with id={}, by user with id={}", unitId, authentication.getId());
        if (unitRepository.exists(unitId)) {
            boolean modifiable = unitService.isValuableDataModifiable(unitId);
            LOG.info(auditMarker, "Unit with id={} modifiable status is {}. Request for user id={}", unitId, modifiable, authentication.getId());
            return ResponseEntity.ok(modifiable);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyPermission('Unit', 'edit')")
    @PostMapping("/{id}")
    public ResponseEntity<Unit> updateUnit(@PathVariable("id") Long unitId, @RequestBody @Validated Unit unit, RestAuthenticationToken authentication) {
        if (unitRepository.existsByIdentifierTypeAndIdentifierValueAndIdNot(unit.getIdentifier().getType(), unit.getIdentifier().getValue(), unitId)) {
            LOG.info(auditMarker, "Unit with identifier type={} and identifier={} already exists", unit.getIdentifier().getType(), unit.getIdentifier().getValue());
            throw new FlowersException(String.format("Unit with identifier type=%s and identifier=%s already exists", unit.getIdentifier().getType(), unit.getIdentifier().getValue()),
                    null,
                    GlobalErrorCodes.DUPLICATE,
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
        LOG.info(auditMarker, "User with id={}, is updating data for unit with id={}", authentication.getId(), unitId);
        if (!unitService.changesCanBeApplied(unit)) {
            LOG.info(auditMarker, "User with id={} has tried to modify not allowed data from unit with id={}", authentication.getId(), unitId);
            throw new FlowersException("Only email and phone number can be modified.", unit, GlobalErrorCodes.UNIT_HAS_RELATED_DATA, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        unit = unitRepository.save(unit);
        LOG.info(auditMarker, "User with id={} modified unit with id={}. New state {}", authentication.getId(), unitId, unit);
        return ResponseEntity.ok(unit);
    }

    @PreAuthorize("hasAnyPermission('Unit', 'edit')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUnit(@PathVariable("id") Long unitId, RestAuthenticationToken authentication) {
        if (!unitRepository.exists(unitId)) {
            return ResponseEntity.notFound().build();
        }
        LOG.info(auditMarker, "User with id={}, is trying to delete unit with id={}", authentication.getId(), unitId);
        if (!unitService.canBeDeleted(unitId)) {
            LOG.info(auditMarker, "User with id={} has tried to delete unit with id={}", authentication.getId(), unitId);
            throw new FlowersException("Can be deleted", null, GlobalErrorCodes.UNIT_HAS_RELATED_DATA, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        unitRepository.delete(unitId);
        LOG.info(auditMarker, "Unit with id={} successfully delete by user id={}", unitId, authentication.getId());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyPermission('Unit', 'create')")
    @GetMapping("/{id}")
    public ResponseEntity<Unit> getUnit(@PathVariable("id") Long unitId, RestAuthenticationToken authentication) {
        LOG.info(auditMarker, "User with id={}, is trying to get data for unit with id={}", authentication.getId(), unitId);
        Unit unit = unitRepository.findOne(unitId);
        if (unit != null) {
            LOG.info(auditMarker, "Unit with id={} found for user id={}", unitId, authentication.getId());
            return ResponseEntity.ok(unit);
        }
        LOG.info(auditMarker, "Unit with id={} doesnt exists. User id={}", unitId, authentication.getId());
        return ResponseEntity.notFound().build();
    }
}
