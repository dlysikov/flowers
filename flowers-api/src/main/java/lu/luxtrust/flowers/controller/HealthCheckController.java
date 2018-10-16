package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.integration.lrs.ws.LrsWS;
import lu.luxtrust.flowers.properties.NotifProperties;
import lu.luxtrust.flowers.tools.HsmHelper;
import lu.luxtrust.nagioshelpers.CheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    private final DataSource dataSource;
    private final JavaMailSenderImpl javaMailSender;
    private final LrsWS lrs;
    private final HsmHelper hsmHelper;
    private final RestTemplate restTemplate;
    private final NotifProperties notifProperties;

    @Autowired
    public HealthCheckController(DataSource dataSource,
                                 JavaMailSenderImpl javaMailSender,
                                 LrsWS lrs,
                                 HsmHelper hsmHelper,
                                 RestTemplate restTemplate,
                                 NotifProperties notifProperties) {
        this.dataSource = dataSource;
        this.javaMailSender = javaMailSender;
        this.lrs = lrs;
        this.hsmHelper = hsmHelper;
        this.restTemplate = restTemplate;
        this.notifProperties = notifProperties;
    }

    @GetMapping
    public String healthCheck() {
        CheckResult result = getOKServiceStatus("flowers-api");

        Optional<CheckResult> first = Stream.of(hsmCheck(), dbCheck(), smtpCheck(), notifCheck(), lrsCheck())
                .filter(cr -> cr != null && cr.getLevel() == CheckResult.ResultLevel.CRITICAL)
                .findFirst();

        if (first.isPresent()) {
            result = first.get();
        }

        return result.getLevel().ordinal() +";" + result.GetNagiosOutput();
    }

    private CheckResult getUnavailableServiceStatus(String serviceName) {
        return new CheckResult(serviceName, CheckResult.ResultLevel.CRITICAL, HttpStatus.SERVICE_UNAVAILABLE.toString());
    }

    private CheckResult getUnavailableServiceStatus(String serviceName, String msg) {
        return new CheckResult(serviceName, CheckResult.ResultLevel.CRITICAL, msg);
    }

    private CheckResult getOKServiceStatus(String serviceName) {
        return new CheckResult(serviceName, CheckResult.ResultLevel.OK, HttpStatus.OK.toString());
    }

    private CheckResult dbCheck() {
        try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("select 1 from dual");
            return getOKServiceStatus("db-connection");
        } catch (SQLException e) {
            return getUnavailableServiceStatus("db-connection");
        }
    }

    private CheckResult smtpCheck() {
        try {
            javaMailSender.testConnection();
            return getOKServiceStatus("smtp-connection");
        } catch (MessagingException e) {
            return getUnavailableServiceStatus("smtp-connection");
        }
    }

    private CheckResult lrsCheck() {
        try {
            lrs.getVersion();
            return getOKServiceStatus("lrs-connection");
        } catch (Exception e) {
            return getUnavailableServiceStatus("lrs-connection");
        }
    }

    private CheckResult hsmCheck() {
        try {
            hsmHelper.reconnect();
            return getOKServiceStatus("hsm-connection");
        } catch(Throwable e) {
            return getUnavailableServiceStatus("hsm-connection", e.getMessage());
        }
    }

    private CheckResult notifCheck() {
        try {
            if (!notifProperties.getEnabled()) {
                return null;
            }

            Object result = restTemplate.execute(notifProperties.getHealthCheck().toURI(), HttpMethod.GET, (r) -> {}, (ResponseExtractor<Object>) response -> {
                byte[] b = new byte[10000];
                response.getBody().read(b);
                return new String(b);
            });

            if (!result.toString().startsWith("0")) {
                return getUnavailableServiceStatus("notification-service", result.toString());
            }

            return getOKServiceStatus("notification-service");
        } catch(Exception e) {
            return getUnavailableServiceStatus("notification-service", e.getMessage());
        }
    }
}
