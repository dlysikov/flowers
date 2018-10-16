package lu.luxtrust.flowers.security.certificate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class X509CertificateAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(X509CertificateAuthenticatedProcessingFilter.class.getName());

    private static final String CERT_PREFIX = "-----BEGIN CERTIFICATE-----";
    private static final String CERT_SUFFIX = "-----END CERTIFICATE-----";

    private final String headerName;
    private final CertificateFactory cf;
    private final RequestMatcher requestMatcher;

    public X509CertificateAuthenticatedProcessingFilter(String headerName, CertificateFactory cf, RequestMatcher requestMatcher) {
        this.headerName = headerName;
        this.cf = cf;
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        if (!requestMatcher.matches(request)) {
            return null;
        }
        String certificate = request.getHeader(this.headerName);
        if (!StringUtils.isEmpty(certificate)) {
            try {
                LOG.info("Authenticating external service by certificate {}", certificate);
                return cf.generateCertificate(new ByteArrayInputStream(normalize(certificate).getBytes()));
            } catch (CertificateException e) {
                LOG.error("Failed to parse certificate {} for external service", certificate);
            }
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }

    private String normalize(String certificate) {
        return CERT_PREFIX + certificate.replace(CERT_PREFIX," ").replace(CERT_SUFFIX," ").replaceAll("\\s+","\n") + CERT_SUFFIX;
    }
}
