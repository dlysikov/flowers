package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

public class JWTRefreshTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JWTRefreshTokenAuthenticationFilter.class);

    private final JWTService jwtService;
    private final UserService userService;

    public JWTRefreshTokenAuthenticationFilter(String defaultFilterProcessesUrl,
                                               JWTService jwtService,
                                               UserService userService) {
        super(defaultFilterProcessesUrl);
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        CertificateDetails certificateDetails = jwtService.retrieveUserCertificateDetailsFromRefreshToken(request);
        if (certificateDetails == null) {
            LOG.warn("Refresh token invalid or expired! {}", jwtService.getRefreshToken(request));
            throw new BadCredentialsException("Refresh token invalid or expired!");
        }
        User user = userService.findActiveBySSN(certificateDetails.getSsn());
        if (user == null) {
            LOG.warn("Unknown user with ssn {}", certificateDetails.getSsn());
            throw new BadCredentialsException("Unknown user");
        }

        List<GrantedAuthority> grantedAuthorities = user.getRoles()
                .stream()
                .map((r) -> new SimpleGrantedAuthority(r.getRoleType().toString()))
                .collect(Collectors.toList());

        RestAuthenticationToken token = new RestAuthenticationToken(user.getSsn(), user.getId(), grantedAuthorities, user.getUnit());
        token.setDetails(certificateDetails);
        token.setDefaultLanguage(user.getDefaultLanguage());
        token.setAuthenticated(Boolean.TRUE);
        return token;
    }
}
