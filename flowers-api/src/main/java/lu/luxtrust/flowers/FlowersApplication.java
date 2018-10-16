package lu.luxtrust.flowers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.security.NoSuchAlgorithmException;

@EntityScan(basePackages = "lu.luxtrust.flowers.entity")
@SpringBootApplication
@EnableScheduling
public class FlowersApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("encrypt.key", "1B48A7F2B56F722E5E609EBB866E247D46ADF46A70C89779");
        return application.sources(FlowersApplication.class);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.setProperty("encrypt.key", "1B48A7F2B56F722E5E609EBB866E247D46ADF46A70C89779");
        SpringApplication.run(FlowersApplication.class, args);
    }
}
