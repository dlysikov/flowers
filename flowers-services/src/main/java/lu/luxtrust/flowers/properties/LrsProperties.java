package lu.luxtrust.flowers.properties;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "lrs")
@Validated
public class LrsProperties {

    @NotEmpty
    private String keyName;
    private String keyPassword;
    @NotEmpty
    private String lrsWsUrl;
    @NotEmpty
    private String publicKeyName;
    @NotEmpty
    private String raNetworkId;
    @NotEmpty
    private String raOperatorId;
    @NotEmpty
    private String raName;
    @NotEmpty
    private String raSurname;
    @NotEmpty
    private String raSerial;

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getLrsWsUrl() {
        return lrsWsUrl;
    }

    public void setLrsWsUrl(String lrsWsUrl) {
        this.lrsWsUrl = lrsWsUrl;
    }

    public String getRaNetworkId() {
        return raNetworkId;
    }

    public void setRaNetworkId(String raNetworkId) {
        this.raNetworkId = raNetworkId;
    }

    public String getRaOperatorId() {
        return raOperatorId;
    }

    public void setRaOperatorId(String raOperatorId) {
        this.raOperatorId = raOperatorId;
    }

    public String getPublicKeyName() {
        return publicKeyName;
    }

    public void setPublicKeyName(String publicKeyName) {
        this.publicKeyName = publicKeyName;
    }

    public String getRaName() {
        return raName;
    }

    public void setRaName(String raName) {
        this.raName = raName;
    }

    public String getRaSurname() {
        return raSurname;
    }

    public void setRaSurname(String raSurname) {
        this.raSurname = raSurname;
    }

    public String getRaSerial() {
        return raSerial;
    }

    public void setRaSerial(String raSerial) {
        this.raSerial = raSerial;
    }
}
