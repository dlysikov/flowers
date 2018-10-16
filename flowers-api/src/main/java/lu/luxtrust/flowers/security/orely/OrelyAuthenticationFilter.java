package lu.luxtrust.flowers.security.orely;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import lu.luxtrust.orely.api.exceptions.ResponseException;
import lu.luxtrust.orely.api.service.ResponseStatus;
import lu.luxtrust.orely.api.service.SAMLResult;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class OrelyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(OrelyAuthenticationFilter.class);
    private static final Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    public static final String SAML_RESPONSE_ATTR_NAME = "SAMLResponse";

    private final OrelyAuthenticationService orelyAuthenticationService;
    private final UserService userService;
    private final OrelyAuthProperties orelyAuthProperties;

    public OrelyAuthenticationFilter(String filterProcessingUrl,
                                     OrelyAuthenticationService orelyAuthenticationService,
                                     OrelyAuthProperties orelyAuthProperties,
                                     UserService userService) {
        super(new AntPathRequestMatcher(filterProcessingUrl, "POST"));
        this.orelyAuthenticationService = orelyAuthenticationService;
        this.userService = userService;
        this.orelyAuthProperties = orelyAuthProperties;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            CertificateDetails details;
            SAMLResult samlResult;
            if (orelyAuthProperties.isMockEnabled()) {
                User mockUser = userService.findActiveBySSN(request.getParameter("ssn"));
                if (mockUser != null) {
                    samlResult = new SAMLResult("mock", "mock", ResponseStatus.OK, DateTime.now(), "mock", new byte[0], "mock", new HashMap<>(), DateTime.now(), "mock");
                    details = CertificateDetails.newBuilder()
                            .certificateType(mockUser.getCertificateType().toString())
                            .ssn(mockUser.getSsn())
                            .givenName(mockUser.getFirstName())
                            .surname(mockUser.getSurName())
                            .build();
                } else {
                    samlResult = new SAMLResult("mock", "mock", ResponseStatus.RESPONDER_AUTHN_FAILED, DateTime.now(), "mock", new byte[0], "mock", new HashMap<>(), DateTime.now(), "mock");
                    details = null;
                }
            } else {
                samlResult = orelyAuthenticationService.parseResponse(request.getParameter(SAML_RESPONSE_ATTR_NAME));
                details = validateAndParseSAMLResult(samlResult, request);
            }

            if (details == null) {
                LOG.info("Failed to parse credentials from ORELY response");
                throw new OrelyAuthenticationException("Cant recognize user details", samlResult.getStatus());
            }

            User user = userService.findActiveBySSN(details.getSsn());
            if (user == null) {
                LOG.info(auditMarker, "no user for ssn {} is present", details.getSsn());
                throw new OrelyAuthenticationException("Unknown user", samlResult.getStatus());
            }

            details = CertificateDetails.newBuilder()
                    .ssn(details.getSsn())
                    .certificateType(details.getCertificateType())
                    .givenName(user.getFirstName())
                    .surname(user.getSurName())
                    .build();

            List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRoleType().toString())).collect(Collectors.toList());
            RestAuthenticationToken token = new RestAuthenticationToken(user.getSsn(), user.getId(), grantedAuthorities, user.getUnit());
            token.setDetails(details);
            token.setAuthenticated(Boolean.TRUE);
            token.setDefaultLanguage(user.getDefaultLanguage());
            return token;
        } catch (ResponseException e) {
            LOG.info("Failed to parse credentials from ORELY response", e);
            throw new OrelyAuthenticationException("Can't parse orely response", e);
        }
    }

    private CertificateDetails validateAndParseSAMLResult(SAMLResult samlResponse, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = "undefined";
        }
        CertificateDetails details = orelyAuthenticationService.retrieveUserCertificateDetails(samlResponse);
        String ssn = (details == null || StringUtils.isEmpty(details.getSsn())) ? "unavailable" : details.getSsn();
        if (!ResponseStatus.OK.equals(samlResponse.getStatus())) {
            LOG.info(auditMarker, "Authentication error! ip: {}, ssn: {}", ipAddress, ssn);
            throw new OrelyAuthenticationException("Invalid response", samlResponse.getStatus());
        }
        if (!orelyAuthenticationService.isAllowedSignerCertificateUsed(samlResponse)) {
            LOG.info(auditMarker, "Signer certificate of the response is not in the trusted list! ip: {}, ssn: {}", ipAddress, ssn);
            throw new OrelyAuthenticationException("Signer certificate of the response is not in the trusted list!", samlResponse.getStatus());
        }
        return details;
    }
}
