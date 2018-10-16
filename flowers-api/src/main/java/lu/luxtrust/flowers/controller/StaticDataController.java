package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Language;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.service.StaticDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/static")
public class StaticDataController {

    private final StaticDataService staticDataService;
    private final UnitRepository unitRepository;

    @Autowired
    public StaticDataController(StaticDataService staticDataService, UnitRepository unitRepository) {
        this.staticDataService = staticDataService;
        this.unitRepository = unitRepository;
    }

    @GetMapping("/countries")
    public ResponseEntity<List<Country>> getAllCountries() {
        return ResponseEntity.ok(staticDataService.getAllCountries());
    }

    @GetMapping("/nationalities")
    public ResponseEntity<List<Nationality>> getAllNationalities() {
        return ResponseEntity.ok(staticDataService.getAllNationalities());
    }

    @GetMapping("/services")
    public ResponseEntity<List<Requestor>> getAllServices() {
        return ResponseEntity.ok(staticDataService.getAllServices());
    }

    @GetMapping("/languages")
    public ResponseEntity<List<Language>> getAllLanguages() {
        return ResponseEntity.ok(staticDataService.getAllLanguages());
    }

    @GetMapping("/services/config/{id}")
    public ResponseEntity<RequestorConfiguration> getRequestorConfig(@PathVariable Long id) {
        return ResponseEntity.ok(staticDataService.getRequestorConfiguration(id));
    }

    @GetMapping("/units")
    public ResponseEntity<List<Unit>> getAllUnits() {
        return ResponseEntity.ok(unitRepository.findAll());
    }
}
