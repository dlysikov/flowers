package lu.luxtrust.flowers.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URL;

@ConfigurationProperties(prefix = "notif-service")
@Validated
public class NotifProperties {

    @NotNull
    private Boolean enabled;

    @NotNull
    private URL endpoint;

    @NotNull
    private URL healthCheck;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public URL getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(URL endpoint) {
        this.endpoint = endpoint;
    }

    public URL getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(URL healthCheck) {
        this.healthCheck = healthCheck;
    }
}
