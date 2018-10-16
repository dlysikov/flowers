package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JWTPreAuthenticatedAuthenticationProviderTest {

    private static final String CERTIFICATE_TYPE = "PRO";
    private static final String SSN = "111111";

    @Mock
    private UserService userService;

    private CertificateDetails certificateDetails;
    private JWTPreAuthenticatedAuthenticationProvider target;

    @Before
    public void init(){
        this.target = new JWTPreAuthenticatedAuthenticationProvider(userService);
        this.certificateDetails = CertificateDetails.newBuilder().certificateType(CERTIFICATE_TYPE).ssn(SSN).build();
    }

    @Test
    public void supportsPreAuthenticatedAuthenticationToken() {
        assertThat(target.supports(PreAuthenticatedAuthenticationToken.class)).isTrue();
    }

    @Test
    public void doesntSupportAuthentication() {
        assertThat(target.supports(Authentication.class)).isFalse();
    }

    @Test(expected = BadCredentialsException.class)
    public void authWithUnknownCertificate() {
        when(userService.findActiveBySSN(SSN)).thenReturn(null);
        target.authenticate(new PreAuthenticatedAuthenticationToken(certificateDetails, new ArrayList<>()));
    }

    @Test
    public void authWithNotCertificateDetails() {
        assertThat(target.authenticate(new PreAuthenticatedAuthenticationToken(1L, new ArrayList<>()))).isNull();;
        verify(userService, never()).findActiveBySSN(anyString());
    }

    @Test
    public void authSuccess() {
        User user = mock(User.class);
        Role role = new Role();
        role.setRoleType(RoleType.END_USER);
        when(user.getRoles()).thenReturn(Sets.newLinkedHashSet(role));
        when(user.getSsn()).thenReturn(SSN);
        when(userService.findActiveBySSN(SSN)).thenReturn(user);

        Authentication authenticate = target.authenticate(new PreAuthenticatedAuthenticationToken(certificateDetails, new ArrayList<>()));

        assertThat(authenticate)
                .isInstanceOf(RestAuthenticationToken.class)
                .hasFieldOrPropertyWithValue("ssn", SSN)
                .hasFieldOrPropertyWithValue("details", certificateDetails)
                .hasFieldOrPropertyWithValue("authenticated", Boolean.TRUE);
        ;
    }
}