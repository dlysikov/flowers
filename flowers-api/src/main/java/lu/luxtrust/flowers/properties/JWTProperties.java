package lu.luxtrust.flowers.properties;

import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "jwt")
@Validated
public class JWTProperties {

    @NotNull
    @Min(1000)
    private Long ttl;
    @NotNull
    @Min(1000)
    private Long refreshTokenTtl;
    @NotNull
    private SignatureAlgorithm signatureAlg;
    @NotEmpty
    private String refreshTokenHeader;
    @NotEmpty
    private String refreshTokenSecret;
    @NotEmpty
    private String secret;
    @NotEmpty
    private String authHeader;
    @NotEmpty
    private String authHeaderPrefix;

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    public SignatureAlgorithm getSignatureAlg() {
        return signatureAlg;
    }

    public void setSignatureAlg(SignatureAlgorithm signatureAlg) {
        this.signatureAlg = signatureAlg;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public String getAuthHeaderPrefix() {
        return authHeaderPrefix;
    }

    public void setAuthHeaderPrefix(String authHeaderPrefix) {
        this.authHeaderPrefix = authHeaderPrefix;
    }

    public String getRefreshTokenSecret() {
        return refreshTokenSecret;
    }

    public void setRefreshTokenSecret(String refreshTokenSecret) {
        this.refreshTokenSecret = refreshTokenSecret;
    }

    public Long getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Long refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String getRefreshTokenHeader() {
        return refreshTokenHeader;
    }

    public void setRefreshTokenHeader(String refreshTokenHeader) {
        this.refreshTokenHeader = refreshTokenHeader;
    }

    @AssertTrue
    public boolean assertRefreshTokenTtlMoreThanTokenTtl() {
        return refreshTokenTtl > ttl;
    }
}
