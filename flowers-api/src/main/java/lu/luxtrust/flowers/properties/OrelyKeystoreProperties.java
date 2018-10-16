package lu.luxtrust.flowers.properties;

import lu.luxtrust.orely.api.factory.KeyStoreType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "orely.provider.sign.keystore")
@Validated
public class OrelyKeystoreProperties {
    @NotEmpty
    private String certificateAlias;
    private String certificatePassword;

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public void setCertificateAlias(String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }

    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
}

