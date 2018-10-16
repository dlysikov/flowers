package lu.luxtrust.flowers.security.jwt;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class JWTPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final JWTService jwtService;
    private final RequestMatcher requestMatcher;

    public JWTPreAuthenticatedProcessingFilter(JWTService jwtService, RequestMatcher requestMatcher) {
        this.jwtService = jwtService;
        this.requestMatcher = requestMatcher;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        if (this.requestMatcher.matches(request)) {
            return jwtService.retrieveUserCertificateDetailsFromToken(request);
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return null;
    }
}
