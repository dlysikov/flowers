package lu.luxtrust.flowers.security.jwt;

import lu.luxtrust.flowers.model.CertificateDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JWTPreAuthenticatedProcessingFilterTest {

    @Mock
    private JWTService jwtService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private RequestMatcher requestMatcher;

    private JWTPreAuthenticatedProcessingFilter target;
    private CertificateDetails certificateDetails;

    @Before
    public void init() {
        this.target = new JWTPreAuthenticatedProcessingFilter(jwtService, requestMatcher);
        this.certificateDetails = CertificateDetails.newBuilder().build();
    }

    @Test
    public void getPreAuthenticatedPrincipalWhenUrlRequestMatchs() {
        when(requestMatcher.matches(httpServletRequest)).thenReturn(Boolean.TRUE);
        when(jwtService.retrieveUserCertificateDetailsFromToken(httpServletRequest)).thenReturn(certificateDetails);

        assertThat(target.getPreAuthenticatedPrincipal(httpServletRequest)).isEqualTo(certificateDetails);
    }

    @Test
    public void getPreAuthenticatedPrincipalWhenUrlRequestNotMatchs() {
        when(requestMatcher.matches(httpServletRequest)).thenReturn(Boolean.FALSE);
        when(jwtService.retrieveUserCertificateDetailsFromToken(httpServletRequest)).thenReturn(certificateDetails);

        assertThat(target.getPreAuthenticatedPrincipal(httpServletRequest)).isNull();
    }

    @Test
    public void getPreAuthenticatedCredentials() {
        assertThat(target.getPreAuthenticatedPrincipal(httpServletRequest)).isNull();
    }
}