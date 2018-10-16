package lu.luxtrust.flowers.properties;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties("orely.provider.sign")
@Validated
public class OrelyProperties {

    @NotEmpty
    private String certificateValidatorPath;

    @NotEmpty
    private List<TrustedCertificateProperties> trustedCertificates;

    public String getCertificateValidatorPath() {
        return certificateValidatorPath;
    }

    public void setCertificateValidatorPath(String certificateValidatorPath) {
        this.certificateValidatorPath = certificateValidatorPath;
    }

    public List<TrustedCertificateProperties> getTrustedCertificates() {
        return trustedCertificates;
    }

    public void setTrustedCertificates(List<TrustedCertificateProperties> trustedCertificates) {
        this.trustedCertificates = trustedCertificates;
    }

    public static class TrustedCertificateProperties {
        @NotBlank
        private String issuer;
        @NotBlank
        private String certificateSerialNumber;

        public String getCertificateSerialNumber() {
            return certificateSerialNumber;
        }

        public void setCertificateSerialNumber(String certificateSerialNumber) {
            this.certificateSerialNumber = certificateSerialNumber;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }
    }
}
