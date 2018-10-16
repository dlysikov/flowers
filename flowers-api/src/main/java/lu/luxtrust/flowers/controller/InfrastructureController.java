package lu.luxtrust.flowers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfrastructureController {

    private final String applicationVersion;

    @Autowired
    public InfrastructureController(@Qualifier("applicationVersion") String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    @GetMapping("/version")
    public String version() {
        return this.applicationVersion;
    }

}
