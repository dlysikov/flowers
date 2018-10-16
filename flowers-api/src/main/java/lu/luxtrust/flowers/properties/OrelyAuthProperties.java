package lu.luxtrust.flowers.properties;

import lu.luxtrust.orely.api.service.CertificateRequest;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.net.URL;

@ConfigurationProperties(prefix = "orely.provider.auth")
@Validated
public class OrelyAuthProperties {
    @NotNull
    private URL issuerUrl;
    @NotNull
    private URL destinationUrl;
    @NotNull
    private URL returnUrl;

    @NotEmpty
    private String tspId;
    @NotEmpty
    private String tspType;

    @NotEmpty
    private String idPrefix;
    @NotNull
    private Integer qaaLevel;
    @NotNull
    private CertificateRequest certificateRequest;
    @NotNull
    private Boolean enableOcsp;
    @NotEmpty
    private String loginProcessingUrl;

    private boolean mockEnabled;

    public Boolean getEnableOcsp() {
        return enableOcsp;
    }

    public String getLoginProcessingUrl() {
        return loginProcessingUrl;
    }

    public void setLoginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
    }

    public URL getIssuerUrl() {
        return issuerUrl;
    }

    public void setIssuerUrl(URL issuerUrl) {
        this.issuerUrl = issuerUrl;
    }

    public URL getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(URL destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public URL getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(URL returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getTspId() {
        return tspId;
    }

    public void setTspId(String tspId) {
        this.tspId = tspId;
    }

    public String getTspType() {
        return tspType;
    }

    public void setTspType(String tspType) {
        this.tspType = tspType;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public Integer getQaaLevel() {
        return qaaLevel;
    }

    public void setQaaLevel(Integer qaaLevel) {
        this.qaaLevel = qaaLevel;
    }

    public CertificateRequest getCertificateRequest() {
        return certificateRequest;
    }

    public void setCertificateRequest(CertificateRequest certificateRequest) {
        this.certificateRequest = certificateRequest;
    }

    public Boolean isEnableOcsp() {
        return enableOcsp;
    }

    public void setEnableOcsp(Boolean enableOcsp) {
        this.enableOcsp = enableOcsp;
    }

    public boolean isMockEnabled() {
        return mockEnabled;
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }
}
