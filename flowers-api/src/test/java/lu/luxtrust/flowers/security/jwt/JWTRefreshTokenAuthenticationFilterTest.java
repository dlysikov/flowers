package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.service.UserService;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JWTRefreshTokenAuthenticationFilterTest {

    private static final String SSN = "11111";

    @Mock
    private JWTService jwtService;
    @Mock
    private CertificateOrderService certificateOrderService;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    private HttpServletRequest httpServletRequest;

    private JWTRefreshTokenAuthenticationFilter target;
    private CertificateDetails details;

    @Before
    public void init() {
        this.target = new JWTRefreshTokenAuthenticationFilter("url", jwtService, userService);
        this.details = CertificateDetails.newBuilder().ssn(SSN).build();
    }

    @Test(expected = BadCredentialsException.class)
    public void authWithoutRefreshToken() throws Exception {
        target.attemptAuthentication(httpServletRequest, httpServletResponse);
    }

    @Test(expected = BadCredentialsException.class)
    public void authWithUnknownCertificate() throws Exception {
        when(jwtService.retrieveUserCertificateDetailsFromRefreshToken(httpServletRequest)).thenReturn(details);
        target.attemptAuthentication(httpServletRequest, httpServletResponse);
    }

    @Test
    public void authSuccess() throws Exception {
        when(jwtService.retrieveUserCertificateDetailsFromRefreshToken(httpServletRequest)).thenReturn(details);

        User user = mock(User.class);
        when(user.getSsn()).thenReturn(SSN);
        Role role = new Role();
        role.setRoleType(RoleType.CSD_AGENT);
        when(user.getRoles()).thenReturn(Sets.newLinkedHashSet(role));
        when(userService.findActiveBySSN(SSN)).thenReturn(user);

        Authentication auth = target.attemptAuthentication(httpServletRequest, httpServletResponse);

        assertThat(auth)
                .isInstanceOf(RestAuthenticationToken.class)
                .hasFieldOrPropertyWithValue("ssn", SSN)
                .hasFieldOrPropertyWithValue("details", details)
                .hasFieldOrPropertyWithValue("authenticated", Boolean.TRUE);
    }
}