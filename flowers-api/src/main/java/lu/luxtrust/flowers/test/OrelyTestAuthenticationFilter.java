package lu.luxtrust.flowers.test;

import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

public class OrelyTestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;

    public OrelyTestAuthenticationFilter(String defaultFilterProcessesUrl, UserService userService) {
        super(defaultFilterProcessesUrl);
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String ssn = request.getParameter("ssn");
        if (!StringUtils.isEmpty(ssn)) {
            User user = userService.findActiveBySSN(ssn);
            if (user != null) {
                List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map((r) -> new SimpleGrantedAuthority(r.toString())).collect(Collectors.toList());
                RestAuthenticationToken token = new RestAuthenticationToken(user.getSsn(), user.getId(), grantedAuthorities, user.getUnit());
                CertificateDetails certificateDetails = CertificateDetails.newBuilder()
                        .ssn(ssn).givenName(user.getFirstName()).surname(user.getSurName()).certificateType(user.getCertificateType().toString())
                        .build();
                token.setDetails(certificateDetails);
                token.setAuthenticated(Boolean.TRUE);
                return token;
            }
        }
        throw new BadCredentialsException("Unknown ssn");
    }
}
