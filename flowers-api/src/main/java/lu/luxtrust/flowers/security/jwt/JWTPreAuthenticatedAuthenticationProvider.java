package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;
import java.util.stream.Collectors;

public class JWTPreAuthenticatedAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JWTPreAuthenticatedAuthenticationProvider.class);

    private UserService userService;

    public JWTPreAuthenticatedAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!authentication.getPrincipal().getClass().equals(CertificateDetails.class)) {
            return null;
        }
        CertificateDetails details = (CertificateDetails) authentication.getPrincipal();

        User user = userService.findActiveBySSN(details.getSsn());
        if (user == null) {
            LOG.warn("Unknown user with ssn {} tried to get access", details.getSsn());
            throw new BadCredentialsException("Unknown user");
        }

        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getRoleType().toString())).collect(Collectors.toList());
        RestAuthenticationToken token = new RestAuthenticationToken(user.getSsn(), user.getId(), grantedAuthorities, user.getUnit());
        token.setDetails(details);
        token.setAuthenticated(Boolean.TRUE);
        token.setDefaultLanguage(user.getDefaultLanguage());

        LOG.info("Request for user {} with roles {} authorized", user.getSsn(), user.getRoles());

        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.equals(authentication);
    }
}
